/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.infox.telas;

import java.sql.*;
import br.com.infox.dal.ModuloConexao;
import java.util.HashMap;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author usuario
 */
public class TelaOS extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    private String tipo;

    /**
     * Creates new form TelaOS
     */
    public TelaOS() {
        initComponents();
        conexao = ModuloConexao.conector();
    }
    
    private void pesquisar_cliente(){
        String pesquisar = "select idcli as Id, nomecli as Nome, telefonecli as Fone from tbclientes where nomecli like ?";
        try {
            pst = conexao.prepareStatement(pesquisar);
            pst.setString(1, txtOSearchNome.getText() + "%");
            rs = pst.executeQuery();
            
            if(rs.next()){
                tblOSCli.setModel(DbUtils.resultSetToTableModel(rs));
            }else{
                //JOptionPane.showMessageDialog(null, "não foi possível realizar a busca. Procure o suporte técnico." );
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
    }
    
    private void clean() {
        txtOSCliID.setText(null);
        txtOSEquip.setText(null);
        txtOSDef.setText(null);
        txtOSServ.setText(null);
        txtOSTecn.setText(null);
        txtOSValor.setText(null);

    }

    private void setar_campos(){
        int setar = tblOSCli.getSelectedRow();
        txtOSCliID.setText(tblOSCli.getValueAt(setar, 0).toString());
    }
    
    private int validation() {
        int validation;
        if (txtOSCliID.getText().isEmpty() || txtOSEquip.getText().isEmpty() || txtOSDef.getText().isEmpty()) {
            validation = 0;
        } else {
            validation = 1;
        }
        return validation;
    }
    
    private void emitirOS(){
        String emitirOS = "insert into tbos(tipo, situacao, equipamento, defeito, servico, tecnico, valor, idcli) values(?,?,?,?,?,?,?,?)";
        try {
            pst = conexao.prepareStatement(emitirOS);
            pst.setString(1, tipo);
            pst.setString(2, cboOSStatus.getSelectedItem().toString());
            pst.setString(3, txtOSEquip.getText());
            pst.setString(4, txtOSDef.getText());
            pst.setString(5, txtOSServ.getText());
            pst.setString(6, txtOSTecn.getText());
            pst.setString(7, txtOSValor.getText().replace(",", ".")); // convertendo a vírgula pelo ponto para a execução no BD.
            pst.setString(8, txtOSCliID.getText());
            
            if(validation() == 1){
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "OS emitida com sucesso");
            
            clean();
            
           
            }else{
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            e.printStackTrace();
        }
            
    }
    
    private void pesquisarOS(){
        String num_OS = JOptionPane.showInputDialog("Número da OS");
        String pesqOS = "select * from tbos where os = ?";
        
        try {
            pst = conexao.prepareStatement(pesqOS);
            pst.setString(1, num_OS);
            rs = pst.executeQuery();
            
            if(rs.next()){
                txtOSId.setText(rs.getString(1));
                txtOSData.setText(rs.getString(2));
                if(rs.getString(3).equals("Orçamento")){
                    // não executa nada pois o radio button orçamento já vem marcado na abertura do frame.
                    tipo = "Orçamento";
                }else if(rs.getString(3).equals("OS")){
                    radBtnOSOrdemServ.setSelected(true);
                    tipo = "OS";
                }
                cboOSStatus.setSelectedItem(rs.getString(4));
                txtOSEquip.setText(rs.getString(5));
                txtOSDef.setText(rs.getString(6));
                txtOSServ.setText(rs.getString(7));
                txtOSTecn.setText(rs.getString(8));
                txtOSValor.setText(rs.getString(9));
                txtOSCliID.setText(rs.getString(10));
                // prevenindo problemas
                btnOSCadastrar.setEnabled(false);
                txtOSearchNome.setEnabled(false);
                tblOSCli.setVisible(false);
                
                
                
                
            }else{
                JOptionPane.showMessageDialog(null, "OS não encontrada");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void alterarOS() {
        String alterarOS = "update tbos set tipo=?, situacao=?, equipamento=?,defeito=?, servico=?, tecnico=?,valor=? where os=?";
        try {
            pst = conexao.prepareStatement(alterarOS);
            pst.setString(1, tipo);
            pst.setString(2, cboOSStatus.getSelectedItem().toString());
            pst.setString(3, txtOSEquip.getText());
            pst.setString(4, txtOSDef.getText());
            pst.setString(5, txtOSServ.getText());
            pst.setString(6, txtOSTecn.getText());
            pst.setString(7, txtOSValor.getText().replace(",", ".")); // convertendo a vírgula pelo ponto para a execução no BD.
            pst.setString(8, txtOSId.getText());
            
            if(validation() == 1){
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "OS alterada com sucesso");
            
            clean();
            txtOSId.setText(null);
            txtOSData.setText(null);
            
            btnOSCadastrar.setEnabled(true);
            txtOSearchNome.setEnabled(true);
            tblOSCli.setVisible(true);
            
            
           
            }else{
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            e.printStackTrace();
        }
    }

    private void excluirOS() {

        String excluirOS = "delete from tbos where os =?";

        try {
            pst = conexao.prepareStatement(excluirOS);
            pst.setString(1, txtOSId.getText());
            if (!(txtOSId.getText().isEmpty())) {
                int confirma = JOptionPane.showConfirmDialog(null, "quer mesmo excluir esta Ordem de Serviço?", "Atenção", JOptionPane.YES_NO_OPTION);
                if (confirma == JOptionPane.YES_OPTION) {
                    int exclusao = pst.executeUpdate();
                    if (exclusao > 0) {
                        JOptionPane.showMessageDialog(null, "OS exluída com sucesso.");
                        clean();
                        txtOSId.setText(null);
                        txtOSData.setText(null);
                        btnOSCadastrar.setEnabled(false);
                        txtOSearchNome.setEnabled(false);
                        tblOSCli.setVisible(false);

                    } else {
                        JOptionPane.showMessageDialog(null, "Não foi possível excluir esta OS");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Nunhuma OS selecionada para excluir!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }
    
    private void imprimirOS(){
         
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão desta OS?", "Atenção", JOptionPane.YES_NO_OPTION );
        
        if(confirma == JOptionPane.YES_OPTION){
            // imprimindo a OS com o JaperReport
            try {
                // usando a classe HashMap para criar um filtro 
                HashMap filtro = new HashMap();
                filtro.put("os",Integer.parseInt(txtOSId.getText())); // "os" é o parâmetro criado no report OS; 
                JasperPrint print = JasperFillManager.fillReport("C:/reports/OS.jasper",filtro, conexao ); // filtro é o parâmetro obtido para gerar o relatório;
                JasperViewer.viewReport(print, false);
                
            } catch(java.lang.NumberFormatException n){
                JOptionPane.showMessageDialog(null, "formato inválido. Selecione uma OS válida para impressão");
            } catch(Exception e){
                JOptionPane.showMessageDialog(null, e);
            }
             
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtOSId = new javax.swing.JTextField();
        txtOSData = new javax.swing.JTextField();
        radBtnOSOrc = new javax.swing.JRadioButton();
        radBtnOSOrdemServ = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        cboOSStatus = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        txtOSearchNome = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtOSCliID = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOSCli = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtOSEquip = new javax.swing.JTextField();
        txtOSDef = new javax.swing.JTextField();
        txtOSServ = new javax.swing.JTextField();
        txtOSTecn = new javax.swing.JTextField();
        txtOSValor = new javax.swing.JTextField();
        btnOSCadastrar = new javax.swing.JButton();
        btnOSBuscar = new javax.swing.JButton();
        btnOSAlterar = new javax.swing.JButton();
        btnOSDeletar = new javax.swing.JButton();
        btnOSImprimir = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("OS");
        setPreferredSize(new java.awt.Dimension(639, 460));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Nº OS");

        jLabel2.setText("Data");

        txtOSId.setEditable(false);

        txtOSData.setEditable(false);
        txtOSData.setFont(new java.awt.Font("Tahoma", 1, 9)); // NOI18N

        buttonGroup1.add(radBtnOSOrc);
        radBtnOSOrc.setText("Orçamento");
        radBtnOSOrc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radBtnOSOrcActionPerformed(evt);
            }
        });

        buttonGroup1.add(radBtnOSOrdemServ);
        radBtnOSOrdemServ.setText("OS");
        radBtnOSOrdemServ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radBtnOSOrdemServActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(txtOSId, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtOSData)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(radBtnOSOrc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(radBtnOSOrdemServ)
                        .addGap(0, 58, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOSId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOSData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radBtnOSOrc)
                    .addComponent(radBtnOSOrdemServ))
                .addGap(15, 15, 15))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Status:");

        cboOSStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Entrega OK", "Orçamento Reprovado", "Aguardando Aprovação", "Aguandando Peças", "Abandonado pelo Cliente", "Na Bancada", "Retornou" }));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Cliente"));

        txtOSearchNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtOSearchNomeKeyReleased(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/search.png"))); // NOI18N

        jLabel5.setText("*Id");

        tblOSCli.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Id", "Nome", "Fone"
            }
        ));
        tblOSCli.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOSCliMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblOSCli);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtOSearchNome, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOSCliID, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(txtOSearchNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(txtOSCliID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel7.setText("*Equipamento");

        jLabel8.setText("*Defeito");

        jLabel9.setText("Serviço");

        jLabel10.setText("Técnico");

        jLabel11.setText("Valor total");

        btnOSCadastrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/create.png"))); // NOI18N
        btnOSCadastrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOSCadastrar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOSCadastrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnOSCadastrarMouseEntered(evt);
            }
        });
        btnOSCadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOSCadastrarActionPerformed(evt);
            }
        });

        btnOSBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/read.png"))); // NOI18N
        btnOSBuscar.setToolTipText("buscar OS");
        btnOSBuscar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOSBuscar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOSBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOSBuscarActionPerformed(evt);
            }
        });

        btnOSAlterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/update.png"))); // NOI18N
        btnOSAlterar.setToolTipText("alterar OS");
        btnOSAlterar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOSAlterar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOSAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOSAlterarActionPerformed(evt);
            }
        });

        btnOSDeletar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/delete.png"))); // NOI18N
        btnOSDeletar.setToolTipText("excluir OS");
        btnOSDeletar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOSDeletar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOSDeletar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOSDeletarActionPerformed(evt);
            }
        });

        btnOSImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/print.png"))); // NOI18N
        btnOSImprimir.setToolTipText("Imprimir OS");
        btnOSImprimir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOSImprimir.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOSImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOSImprimirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cboOSStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnOSCadastrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(35, 35, 35)
                                        .addComponent(btnOSBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(31, 31, 31)
                                        .addComponent(btnOSAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                                        .addComponent(btnOSDeletar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(28, 28, 28)
                                        .addComponent(btnOSImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(57, 57, 57))))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel9))
                            .addComponent(jLabel10))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtOSTecn, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(txtOSValor, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtOSServ, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                                .addComponent(txtOSDef))
                            .addComponent(txtOSEquip, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cboOSStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtOSEquip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtOSDef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtOSServ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(txtOSTecn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOSValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOSCadastrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOSBuscar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOSAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOSDeletar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOSImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(51, Short.MAX_VALUE))
        );

        setBounds(0, 0, 639, 460);
    }// </editor-fold>//GEN-END:initComponents

    private void txtOSearchNomeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOSearchNomeKeyReleased
        // TODO add your handling code here:
        pesquisar_cliente();
    }//GEN-LAST:event_txtOSearchNomeKeyReleased

    private void tblOSCliMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOSCliMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tblOSCliMouseClicked

    private void radBtnOSOrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radBtnOSOrcActionPerformed
        tipo = radBtnOSOrc.getText();
    }//GEN-LAST:event_radBtnOSOrcActionPerformed

    private void radBtnOSOrdemServActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radBtnOSOrdemServActionPerformed
        tipo = "OS";
    }//GEN-LAST:event_radBtnOSOrdemServActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // Quando o form for aberto, marca o radio button
        radBtnOSOrc.setSelected(true);
        tipo = "Orçamento";
        txtOSValor.setText("0"); // evitar erro de formato decimal no banco de dados;
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnOSCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOSCadastrarActionPerformed
        emitirOS();
    }//GEN-LAST:event_btnOSCadastrarActionPerformed

    private void btnOSBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOSBuscarActionPerformed
        pesquisarOS();
    }//GEN-LAST:event_btnOSBuscarActionPerformed

    private void btnOSAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOSAlterarActionPerformed
        alterarOS();
    }//GEN-LAST:event_btnOSAlterarActionPerformed

    private void btnOSDeletarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOSDeletarActionPerformed
        excluirOS();
    }//GEN-LAST:event_btnOSDeletarActionPerformed

    private void btnOSCadastrarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnOSCadastrarMouseEntered
        btnOSCadastrar.setToolTipText("cadastrar OS");
    }//GEN-LAST:event_btnOSCadastrarMouseEntered

    private void btnOSImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOSImprimirActionPerformed
        // TODO add your handling code here:
        imprimirOS();
    }//GEN-LAST:event_btnOSImprimirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOSAlterar;
    private javax.swing.JButton btnOSBuscar;
    private javax.swing.JButton btnOSCadastrar;
    private javax.swing.JButton btnOSDeletar;
    private javax.swing.JButton btnOSImprimir;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboOSStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton radBtnOSOrc;
    private javax.swing.JRadioButton radBtnOSOrdemServ;
    private javax.swing.JTable tblOSCli;
    private javax.swing.JLabel txtOSCliID;
    private javax.swing.JTextField txtOSData;
    private javax.swing.JTextField txtOSDef;
    private javax.swing.JTextField txtOSEquip;
    private javax.swing.JTextField txtOSId;
    private javax.swing.JTextField txtOSServ;
    private javax.swing.JTextField txtOSTecn;
    private javax.swing.JTextField txtOSValor;
    private javax.swing.JTextField txtOSearchNome;
    // End of variables declaration//GEN-END:variables
}
