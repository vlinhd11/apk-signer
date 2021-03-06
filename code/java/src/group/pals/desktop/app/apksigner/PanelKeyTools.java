/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner;

import group.pals.desktop.app.apksigner.i18n.Messages;
import group.pals.desktop.app.apksigner.i18n.R;
import group.pals.desktop.app.apksigner.utils.Files;
import group.pals.desktop.app.apksigner.utils.KeyTools;
import group.pals.desktop.app.apksigner.utils.Preferences;
import group.pals.desktop.app.apksigner.utils.Texts;
import group.pals.desktop.app.apksigner.utils.ui.Dlg;
import group.pals.desktop.app.apksigner.utils.ui.FileDrop;
import group.pals.desktop.app.apksigner.utils.ui.JEditorPopupMenu;
import group.pals.desktop.app.apksigner.utils.ui.UI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/**
 * Panel for keystore utilities.
 * 
 * @author Hai Bison
 * @since v1.6 beta
 */
public class PanelKeyTools extends JPanel {

    /**
     * Auto-generated by Eclipse.
     */
    private static final long serialVersionUID = 416508787192252476L;

    /**
     * The class name.
     */
    private static final String CLASSNAME = PanelKeyTools.class.getName();

    /**
     * This key holds the last working directory.
     */
    private static final String PKEY_LAST_WORKING_DIR = CLASSNAME
            + ".last_working_dir";

    /*
     * FIELDS
     */

    private File mKeyfile;

    /*
     * CONTROLS
     */

    private JPasswordField mTextPassword;
    private JButton mBtnChooseKeyfile;
    private JButton mBtnListEntries;
    private JTextArea mTextInfo;
    private JScrollPane mTextInfoScrollPane;
    private JPanel mPanelKeyFile;
    private JPanel panel_1;
    private JPanel panel_2;
    @SuppressWarnings("rawtypes")
    private JComboBox mCbxKeystoreType;

    /**
     * Create the panel.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PanelKeyTools() {
        SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);

        mPanelKeyFile = new JPanel();
        springLayout.putConstraint(SpringLayout.NORTH, mPanelKeyFile, 5,
                SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, mPanelKeyFile, 3,
                SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, mPanelKeyFile, -3,
                SpringLayout.EAST, this);
        add(mPanelKeyFile);
        mPanelKeyFile.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        new FileDrop(mPanelKeyFile, UI.BORDER_FILE_DROP,
                mCompKeyFileFileDropListener);

        mBtnChooseKeyfile = new JButton(
                Messages.getString(R.string.desc_load_key_file));
        mPanelKeyFile.add(mBtnChooseKeyfile);
        mBtnChooseKeyfile.addActionListener(mBtnChooseKeyfileActionListener);
        new FileDrop(mBtnChooseKeyfile, BorderFactory.createCompoundBorder(
                UI.BORDER_FILE_DROP, mBtnChooseKeyfile.getBorder()),
                mCompKeyFileFileDropListener);

        panel_1 = new JPanel();
        springLayout.putConstraint(SpringLayout.NORTH, panel_1, 3,
                SpringLayout.SOUTH, mPanelKeyFile);
        springLayout.putConstraint(SpringLayout.WEST, panel_1, 3,
                SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, panel_1, -3,
                SpringLayout.EAST, this);
        add(panel_1);
        panel_1.setLayout(new BorderLayout(10, 5));

        mTextPassword = new JPasswordField();
        panel_1.add(mTextPassword);
        mTextPassword.setHorizontalAlignment(SwingConstants.CENTER);
        mTextPassword.setBorder(new TitledBorder(null, Messages
                .getString(R.string.password), TitledBorder.LEADING,
                TitledBorder.TOP, null, null));

        panel_2 = new JPanel();
        springLayout.putConstraint(SpringLayout.NORTH, panel_2, 5,
                SpringLayout.SOUTH, panel_1);

        JPanel panel_3 = new JPanel();
        panel_1.add(panel_3, BorderLayout.WEST);
        panel_3.setLayout(new BorderLayout(3, 3));

        JLabel lblNewLabel = new JLabel(
                Messages.getString(R.string.keystore_type));
        panel_3.add(lblNewLabel, BorderLayout.NORTH);

        mCbxKeystoreType = new JComboBox();
        lblNewLabel.setLabelFor(mCbxKeystoreType);
        mCbxKeystoreType.setModel(mCbxKeystoreTypeModel);
        panel_3.add(mCbxKeystoreType);

        springLayout.putConstraint(SpringLayout.WEST, panel_2, 3,
                SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, panel_2, -3,
                SpringLayout.EAST, this);
        add(panel_2);
        panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        mBtnListEntries = new JButton(Messages.getString(R.string.list_entries));
        panel_2.add(mBtnListEntries);
        mBtnListEntries.addActionListener(mBtnListEntriesActionListener);

        mTextInfoScrollPane = new JScrollPane();
        springLayout.putConstraint(SpringLayout.NORTH, mTextInfoScrollPane, 5,
                SpringLayout.SOUTH, panel_2);
        springLayout.putConstraint(SpringLayout.WEST, mTextInfoScrollPane, 3,
                SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.SOUTH, mTextInfoScrollPane, -3,
                SpringLayout.SOUTH, this);
        springLayout.putConstraint(SpringLayout.EAST, mTextInfoScrollPane, -3,
                SpringLayout.EAST, this);
        add(mTextInfoScrollPane);

        mTextInfo = new JTextArea();
        mTextInfo.setEditable(false);
        if (!Beans.isDesignTime())
            mTextInfo.setFont(Assets.getDefaultMonoFont());
        mTextInfo.setMargin(new Insets(9, 9, 9, 9));
        mTextInfo.setTabSize(UI.TEXT_COMPONENT_TAB_SIZE);
        mTextInfoScrollPane.setViewportView(mTextInfo);

        JEditorPopupMenu.apply(this);
    }// PanelKeyTools()

    /**
     * Validates all fields.
     * 
     * @return {@code true} or {@code false}.
     */
    private boolean validateFields() {
        if (mKeyfile == null || !mKeyfile.isFile() || !mKeyfile.canRead()) {
            Dlg.showErrMsg(Messages
                    .getString(R.string.msg_keyfile_doesnt_exist));
            mBtnChooseKeyfile.requestFocus();
            return false;
        }

        if (mTextPassword.getPassword() == null
                || mTextPassword.getPassword().length == 0) {
            Dlg.showErrMsg(Messages.getString(R.string.msg_password_is_empty));
            mTextPassword.requestFocus();
            return false;
        }

        return true;
    }// validateFields()

    /**
     * Lists all entries of a keyfile.
     * <p>
     * <b>Notes:</b> You should call {@link #validateFields()} first.
     * </p>
     */
    private void listEntries() {
        CharSequence result = KeyTools.listEntries(mKeyfile,
                String.valueOf(mCbxKeystoreType.getSelectedItem()),
                mTextPassword.getPassword());
        mTextInfo.setText(result.toString().trim());

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                mTextInfoScrollPane.getHorizontalScrollBar().setValue(0);
                mTextInfoScrollPane.getVerticalScrollBar().setValue(0);
            }// run()
        });
    }// listEntries()

    /**
     * Sets the key file.
     * 
     * @param file
     *            the key file.
     */
    private void setKeyFile(File file) {
        if (file != null && file.isFile()) {
            mKeyfile = file;

            mBtnChooseKeyfile.setText(mKeyfile.getName());
            mBtnChooseKeyfile.setForeground(UI.COLOUR_SELECTED_FILE);
            Preferences.getInstance().set(PKEY_LAST_WORKING_DIR,
                    mKeyfile.getParentFile().getAbsolutePath());

            mTextPassword.requestFocus();
        } else {
            mKeyfile = null;

            mBtnChooseKeyfile.setText(Messages
                    .getString(R.string.desc_load_key_file));
            mBtnChooseKeyfile.setForeground(UI.COLOUR_WAITING_CMD);
        }
    }// setKeyFile()

    /*
     * LISTENERS & DATA
     */

    private final FileDrop.Listener mCompKeyFileFileDropListener = new FileDrop.Listener() {

        @Override
        public void onFilesDropped(File[] files) {
            setKeyFile(files[0]);
        }// onFilesDropped()
    };// mCompKeyFileFileDropListener

    private final ActionListener mBtnChooseKeyfileActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            setKeyFile(Files.chooseFile(
                    new File(Preferences.getInstance().get(
                            PKEY_LAST_WORKING_DIR, "/")),
                    Texts.REGEX_KEYSTORE_FILES,
                    Messages.getString(R.string.desc_keystore_files)));
        }// actionPerformed()
    };// mBtnChooseKeyfileActionListener

    private final ActionListener mBtnListEntriesActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (validateFields())
                listEntries();
        }// actionPerformed()
    };// mBtnListEntriesActionListener

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static final DefaultComboBoxModel mCbxKeystoreTypeModel = new DefaultComboBoxModel(
            new Object[] { new Object() {

                @Override
                public String toString() {
                    return KeyTools.KEYSTORE_TYPE_JKS;
                }
            }, new Object() {

                @Override
                public String toString() {
                    return KeyTools.KEYSTORE_TYPE_JCEKS;
                }
            }, new Object() {

                @Override
                public String toString() {
                    return KeyTools.KEYSTORE_TYPE_PKCS12;
                }
            } });// mCbxKeystoreType

}
