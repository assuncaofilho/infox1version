/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.infox.telas;

import br.com.infox.dal.ModuloConexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author usuario
 */
public class TelaCliente extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form TelaCliente
     */
    public TelaCliente() {

        initComponents();
        conexao = ModuloConexao.conector();
        tblClientes.setModel( new DefaultTableModel()); 
        System.out.println("a conexão com dbinfox foi efetuada novamente\n :"
                + conexao);
    }

    private void clean() {
        txtCliID.setText(null);
        txtCliNome.setText(null);
        txtCliFone.setText(null);
        txtCliEmail.setText(null);
        txtCliEnd.setText(null);

    }
    
    private void refresh_telaCli(){
        clean();
        
        tblClientes.setModel( new DefaultTableModel()); //** COMO LIMPAR A TABELA SEM DAR UM NULL EXCEPTION?
        txtCliSearch.setText(null);
        btnCliCreate.setEnabled(true);
        
    }

    private int validation() {
        int validation;
        if (txtCliNome.getText().isEmpty() || txtCliFone.getText().isEmpty() || txtCliEmail.getText().isEmpty()) {
            validation = 0;
        } else {
            validation = 1;
        }
        return validation;
    }

    private void adicionar() {
        String adicionar = "insert into tbclientes (idcli, nomecli, endcli, telefonecli, emailcli) "
                + "values (default, ?, ?, ?, ?)";
        try {
            pst = conexao.prepareStatement(adicionar);
            // substituindo os campos ? da query adicionar;
            pst.setString(1, txtCliNome.getText());
            pst.setString(2, txtCliEnd.getText());
            pst.setString(3, txtCliFone.getText());
            pst.setString(4, txtCliEmail.getText());

            if (validation() == 1) {
                // atualizando a tabela com os dados do formulário
                int verificador = pst.executeUpdate(); // retorna 1 para DML (Insert, Update or Delete);

                if (verificador == 1) { // se a query foi executada então o admin/gerente é notificado

                    JOptionPane.showMessageDialog(null, "Usuário cadastrado com sucesso");
                    clean();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Não foi possível cadastrar o usuário. \n Verifique os campos obrigatórios.");

            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, "este email já existe em nosso cadastro. Informe um email diferente");
            //e.printStackTrace();

        } catch (Exception e1){
            JOptionPane.showMessageDialog(null, e1);
            e1.printStackTrace();
        }
    }

    public void pesquisar_cliente() {
        String pesquisar = "select * from tbclientes where nomecli like ?";
        try {
            pst = conexao.prepareStatement(pesquisar);
            pst.setString(1, txtCliSearch.getText() + "%");
            rs = pst.executeQuery();

            //rs.next();
            //System.out.println("o valor da primeira coluna é " + rs.getString(1));
            //utilizando  abaixo o recurso de busca avançada da bibioteca rs2xml.jar 
            tblClientes.setModel(DbUtils.resultSetToTableModel(rs));
            // PROVAVELMENTE ESTE MÉTODO DbUtils.resultSetTableModel(rs) define que 
            // a primeira coluna da tabela já não é idcli e sim nomecli. Seria por causa
            // do idcli auto_increment? Ou é uma configuração padrão da implementação?
            // Pois está-se setando o modelo da tabela;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void setar_campos() {
        int setar = tblClientes.getSelectedRow();
        //txtCliID.setText(tblClientes.getModel().getValueAt(setar, 0).toString());
        // parece ser dispensável a utilização do getModel() já que o modelo já foi setado em pesquisar_cliente().
        txtCliID.setText(tblClientes.getValueAt(setar, 0).toString());
        txtCliNome.setText(tblClientes.getValueAt(setar, 1).toString());
        txtCliEnd.setText(tblClientes.getValueAt(setar, 2).toString());
        txtCliFone.setText(tblClientes.getValueAt(setar, 3).toString());
        txtCliEmail.setText(tblClientes.getValueAt(setar, 4).toString());
        btnCliCreate.setEnabled(false); // cadastro fica false

    }

    private void alterar() {
        String alterar = "update tbclientes set nomecli =?, endcli=?, telefonecli=?, emailcli=? where idcli=?";
        if(!(txtCliID.getText().isEmpty())){ // se o campo IDCli não estiver vazio o método prossegue;
        try {
            pst = conexao.prepareStatement(alterar);
            pst.setString(1, txtCliNome.getText());
            pst.setString(2, txtCliEnd.getText());
            pst.setString(3, txtCliFone.getText());
            pst.setString(4, txtCliEmail.getText());
            pst.setString(5, tblClientes.getValueAt(tblClientes.getSelectedRow(), 0).toString());

            if (validation() == 1) {
                int verificador = pst.executeUpdate();
                if (verificador == 1) { // se a execução for exitosa de uma DML (insert, upedate, delete or drop)
                    JOptionPane.showMessageDialog(null, "Dados alterados com sucesso!");
                    clean();
                    btnCliCreate.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(null, "falha na alteração dos dados. Tente novamente.");

                }
            } else {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            e.printStackTrace();
        }
      } else{
            JOptionPane.showMessageDialog(null, "Selecione um cliente para alterar");
        }  
    }

    private void remover() {

        try {
            // não é mais necessário fazer a verificação abaixo pois na TelaCliente o id é inalterável.
            //if (txtUsuID.getText().equals(rs.getString("iduser"))) { // verifica se o usuário do programa 
            // consultou um id x e acidentalmente trocou o para id y e deseja remover o id x.
            
            if(!(txtCliID.getText().isEmpty())){

            int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover " + txtCliNome.getText() + " ?", "Atenção", JOptionPane.YES_NO_OPTION);
            if (confirma == JOptionPane.YES_OPTION) {
                String remover = "delete from tbclientes where idcli=?";
                try {
                    pst = conexao.prepareStatement(remover);
                    pst.setString(1, txtCliID.getText());
                    int verificador = pst.executeUpdate(); // retorna 1 para DML (insert, update, delete, drop);
                    if (verificador == 1) {
                        JOptionPane.showMessageDialog(null, "Usuário removido com sucesso.");
                        clean();
                        btnCliCreate.setEnabled(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Não foi possível remover este usuário. Por favor tente novamente.\n");
                        clean();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }

            }
          }else{
                JOptionPane.showMessageDialog(null, "Nenhum cliente selecionado para ser removido");
            }  
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtCliNome = new javax.swing.JTextField();
        txtCliEnd = new javax.swing.JTextField();
        txtCliFone = new javax.swing.JTextField();
        txtCliEmail = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnCliCreate = new javax.swing.JButton();
        btnCliUpdate = new javax.swing.JButton();
        btnCliDelete = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtCliSearch = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        txtCliID = new javax.swing.JTextField();
        btnCliRefresh = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setPreferredSize(new java.awt.Dimension(640, 460));

        jLabel1.setText("*Nome");

        jLabel2.setText("Endereço");

        jLabel3.setText("*telefone");

        jLabel4.setText("*email");

        btnCliCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/create.png"))); // NOI18N
        btnCliCreate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliCreate.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCliCreate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCliCreateMouseEntered(evt);
            }
        });
        btnCliCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliCreateActionPerformed(evt);
            }
        });

        btnCliUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/update.png"))); // NOI18N
        btnCliUpdate.setToolTipText("");
        btnCliUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliUpdate.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCliUpdate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCliUpdateMouseEntered(evt);
            }
        });
        btnCliUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliUpdateActionPerformed(evt);
            }
        });

        btnCliDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/delete.png"))); // NOI18N
        btnCliDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliDelete.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCliDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCliDeleteMouseEntered(evt);
            }
        });
        btnCliDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliDeleteActionPerformed(evt);
            }
        });

        jLabel5.setText("* Campos obrigatórios");

        txtCliSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCliSearchKeyReleased(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/search.png"))); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(40, 40));

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblClientes);

        jLabel7.setText("id_cliente");

        txtCliID.setEnabled(false);

        btnCliRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/REFRESH.png"))); // NOI18N
        btnCliRefresh.setToolTipText("Refresh");
        btnCliRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliRefresh.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCliRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(73, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtCliID, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCliSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addGap(18, 18, 18)
                            .addComponent(txtCliFone))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel1)
                                .addComponent(jLabel4))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtCliEmail)
                                .addComponent(txtCliNome, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(18, 18, 18)
                            .addComponent(txtCliEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(btnCliCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(43, 43, 43)
                            .addComponent(btnCliUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(45, 45, 45)
                            .addComponent(btnCliDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCliRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(57, 57, 57))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(txtCliSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCliID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCliNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCliEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCliFone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCliEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCliUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCliDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCliCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCliRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(53, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, txtCliEmail, txtCliEnd, txtCliFone, txtCliNome});

        setBounds(0, 0, 639, 460);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCliCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliCreateActionPerformed
        // Adicionando clientes em tbclientes
        adicionar();
    }//GEN-LAST:event_btnCliCreateActionPerformed

    private void btnCliCreateMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCliCreateMouseEntered
        // Informando a função de cadastrar um novo cliente
        btnCliCreate.setToolTipText("Cadastrar");

    }//GEN-LAST:event_btnCliCreateMouseEntered

    private void btnCliUpdateMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCliUpdateMouseEntered
        // Informando a função de alterar os dados de um cliente já cadastrado
        btnCliUpdate.setToolTipText("Salvar alterações");
    }//GEN-LAST:event_btnCliUpdateMouseEntered

    private void btnCliDeleteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCliDeleteMouseEntered
        // Informando a função de remover um cliente cadastrado
        btnCliDelete.setToolTipText("Remover este cliente");
    }//GEN-LAST:event_btnCliDeleteMouseEntered
// o evento abaixo é do tipo "enquanto for digitando"
    private void txtCliSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliSearchKeyReleased
        //chamar o método pesquisar_cliente();
        pesquisar_cliente();

    }//GEN-LAST:event_txtCliSearchKeyReleased

    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
        setar_campos();


    }//GEN-LAST:event_tblClientesMouseClicked

    private void btnCliUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliUpdateActionPerformed
        // chamar o método alterar
        alterar();
    }//GEN-LAST:event_btnCliUpdateActionPerformed

    private void btnCliDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliDeleteActionPerformed
        // TODO add your handling code here:
        remover();
    }//GEN-LAST:event_btnCliDeleteActionPerformed

    private void btnCliRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliRefreshActionPerformed
        // TODO add your handling code here:
        refresh_telaCli();
    }//GEN-LAST:event_btnCliRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCliCreate;
    private javax.swing.JButton btnCliDelete;
    private javax.swing.JButton btnCliRefresh;
    private javax.swing.JButton btnCliUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtCliEmail;
    private javax.swing.JTextField txtCliEnd;
    private javax.swing.JTextField txtCliFone;
    private javax.swing.JTextField txtCliID;
    private javax.swing.JTextField txtCliNome;
    private javax.swing.JTextField txtCliSearch;
    // End of variables declaration//GEN-END:variables
}
