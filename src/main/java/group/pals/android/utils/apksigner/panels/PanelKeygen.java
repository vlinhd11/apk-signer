/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanelKeygen.java
 *
 * Created on Feb 15, 2012, 5:48:38 PM
 */
package group.pals.android.utils.apksigner.panels;

import group.pals.android.utils.apksigner.MainFrame;
import group.pals.android.utils.apksigner.utils.Files;
import group.pals.android.utils.apksigner.utils.KeyGen;
import group.pals.android.utils.apksigner.utils.MsgBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 *
 * @author root
 */
public class PanelKeygen extends javax.swing.JPanel {

    private final MainFrame MF;

    /** Creates new form PanelKeygen */
    public PanelKeygen(MainFrame m) {
        MF = m;

        initComponents();
        btnChooseFile.addActionListener(BtnChooseFileListener);
        btnGenFile.addActionListener(BtnGenKeyFileListener);
        txtValidity.setValue(25);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        txtFile = new javax.swing.JTextField();
        btnChooseFile = new javax.swing.JButton();
        txtPwd = new javax.swing.JPasswordField();
        txtPwd2 = new javax.swing.JPasswordField();
        txtAlias = new javax.swing.JTextField();
        txtAliasPwd = new javax.swing.JPasswordField();
        txtAliasPwd2 = new javax.swing.JPasswordField();
        txtValidity = new javax.swing.JSpinner();
        txtName = new javax.swing.JTextField();
        txtOrgUnit = new javax.swing.JTextField();
        txtOrg = new javax.swing.JTextField();
        txtCity = new javax.swing.JTextField();
        txtState = new javax.swing.JTextField();
        txtCountry = new javax.swing.JTextField();
        btnGenFile = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        txtFile.setBorder(javax.swing.BorderFactory.createTitledBorder("Target file:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(txtFile, gridBagConstraints);

        btnChooseFile.setText("Choose...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        add(btnChooseFile, gridBagConstraints);

        txtPwd.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPwd.setBorder(javax.swing.BorderFactory.createTitledBorder("Password:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(txtPwd, gridBagConstraints);

        txtPwd2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPwd2.setBorder(javax.swing.BorderFactory.createTitledBorder("Retype:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(txtPwd2, gridBagConstraints);

        txtAlias.setBorder(javax.swing.BorderFactory.createTitledBorder("Alias:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(txtAlias, gridBagConstraints);

        txtAliasPwd.setBorder(javax.swing.BorderFactory.createTitledBorder("Alias password:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(txtAliasPwd, gridBagConstraints);

        txtAliasPwd2.setBorder(javax.swing.BorderFactory.createTitledBorder("Confirm:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(txtAliasPwd2, gridBagConstraints);

        txtValidity.setBorder(javax.swing.BorderFactory.createTitledBorder("Validity (years - 25 is recommended):"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(txtValidity, gridBagConstraints);

        txtName.setBorder(javax.swing.BorderFactory.createTitledBorder("First and last name:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(txtName, gridBagConstraints);

        txtOrgUnit.setBorder(javax.swing.BorderFactory.createTitledBorder("Organizational unit:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(txtOrgUnit, gridBagConstraints);

        txtOrg.setBorder(javax.swing.BorderFactory.createTitledBorder("Organization:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(txtOrg, gridBagConstraints);

        txtCity.setBorder(javax.swing.BorderFactory.createTitledBorder("City or locality:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(txtCity, gridBagConstraints);

        txtState.setBorder(javax.swing.BorderFactory.createTitledBorder("State or province:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 0.5;
        add(txtState, gridBagConstraints);

        txtCountry.setBorder(javax.swing.BorderFactory.createTitledBorder("Country code (XX):"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 0.5;
        add(txtCountry, gridBagConstraints);

        btnGenFile.setText("Generate KeyFile");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        add(btnGenFile, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseFile;
    private javax.swing.JButton btnGenFile;
    private javax.swing.JTextField txtAlias;
    private javax.swing.JPasswordField txtAliasPwd;
    private javax.swing.JPasswordField txtAliasPwd2;
    private javax.swing.JTextField txtCity;
    private javax.swing.JTextField txtCountry;
    private javax.swing.JTextField txtFile;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtOrg;
    private javax.swing.JTextField txtOrgUnit;
    private javax.swing.JPasswordField txtPwd;
    private javax.swing.JPasswordField txtPwd2;
    private javax.swing.JTextField txtState;
    private javax.swing.JSpinner txtValidity;
    // End of variables declaration//GEN-END:variables

    /*
     * ACTION LISTENERS
     */
    private final ActionListener BtnChooseFileListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            File file = Files.chooseFile(null);
            if (file != null) {
                txtFile.setText(file.getAbsolutePath());
            }
        }
    };//BtnChooseFileListener
    private final ActionListener BtnGenKeyFileListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            File file = new File(txtFile.getText());
            if (!file.getParentFile().isDirectory()) {
                MsgBox.showErrMsg(null, null, "Target folder does not exist!");
                return;
            }

            String pwd = new String(txtPwd.getPassword());
            if (pwd.isEmpty()) {
                MsgBox.showErrMsg(null, null, "Password can not be empty!");
                return;
            }
            if (!pwd.equals(new String(txtPwd2.getPassword()))) {
                MsgBox.showErrMsg(null, null, "Password does not match!");
                return;
            }

            String alias = txtAlias.getText().trim();
            if (alias.isEmpty()) {
                MsgBox.showErrMsg(null, null, "Alias is empty!");
                return;
            }

            String aliasPwd = new String(txtAliasPwd.getPassword());
            if (aliasPwd.isEmpty()) {
                MsgBox.showErrMsg(null, null, "Alias password can not be empty!");
                return;
            }
            if (!aliasPwd.equals(new String(txtAliasPwd2.getPassword()))) {
                MsgBox.showErrMsg(null, null, "Alias password does not match!");
                return;
            }

            int validity = (Integer) txtValidity.getValue();
            if (validity <= 0) {
                MsgBox.showErrMsg(null, null, "Validity must be > 0!");
                return;
            }

            String name = txtName.getText().trim();
            String orgUnit = txtOrgUnit.getText().trim();
            String org = txtOrg.getText().trim();
            String city = txtCity.getText().trim();
            String state = txtState.getText().trim();
            String country = txtCountry.getText().trim();
            String[] ownerProps = new String[]{name, orgUnit, org, city, state, country};

            int validValueCount = 0;
            for (String s : ownerProps) {
                if (!s.isEmpty()) {
                    validValueCount++;
                }
            }
            if (validValueCount < 1) {
                MsgBox.showErrMsg(null, null, "At least one Certificate issuer field is required to be non-empty.");
                return;
            }

            try {
                if (file.exists()) {
                    file.delete();
                }

                KeyGen.genKey(MF.getJdkDir(), file, pwd, alias, aliasPwd, validity * 365, name, orgUnit, org, city, state, country);
                MsgBox.showInfoMsg(null, null, "Key-file generated successfully");
            } catch (Exception ex) {
                MsgBox.showErrMsg(null, null, "Error while generating key-file. Please try again.\n\nDetails:\n" + ex);
            }
        }
    };//BtnGenKeyFileListener
}
