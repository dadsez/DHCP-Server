/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.javeriana.appServidorDHCP.presentacion;


import co.edu.javeriana.appServidorDHCP.negocio.Asignacion;
import co.edu.javeriana.appServidorDHCP.negocio.Red;
import co.edu.javeriana.appServidorDHCP.negocio.Sistema;
import co.edu.javeriana.appServidorDHCP.persistencia.ManejoArchivos;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author DAVID FELIPE
 */
public class GUIServidorDHCP extends javax.swing.JFrame
{

  private final String[] columnsIpAsiganadas;
  private static Refresca refr;
  public static Envia env;
  /**
   * Creates new form GUIServidorDHCP
   * @throws java.net.UnknownHostException
   */
  public GUIServidorDHCP() throws UnknownHostException
  {
    this.columnsIpAsiganadas = new String[6];
    this.columnsIpAsiganadas[0] = "No.";
    this.columnsIpAsiganadas[1] = " IP ASIGNADA";
    this.columnsIpAsiganadas[2] = " MAC";
    this.columnsIpAsiganadas[3] = " ESTADO";
    this.columnsIpAsiganadas[4] = " HORA INICIO";
    this.columnsIpAsiganadas[5] = " HORA FIN";
    initComponents();
    refr = new Refresca();
    env = new Envia();
    this.refrescarTabla();
  }


  public class Refresca extends Thread
  {

    public boolean correr;
    public Refresca()
    {
      this.correr = true;
    }

    @Override
    public void run()
    {
      while ( this.correr )
      {
        try
        {
          Thread.sleep( 14000 );
        }
        catch ( InterruptedException ex )
        {
          Logger.getLogger( GUIServidorDHCP.class.getName() ).log( Level.SEVERE, null, ex );
        }
        refrescarTabla();

      }
    }

  }


  public class Envia extends Thread
  {

    public Sistema sistema;
    public Envia()
    {
      try
      {
        this.sistema = new Sistema();
        ManejoArchivos.leerConfServidor( sistema );
      }
      catch ( UnknownHostException ex )
      {
        Logger.getLogger( GUIServidorDHCP.class.getName() ).log( Level.SEVERE, null, ex );
      }
      catch ( Exception ex )
      {
        Logger.getLogger( GUIServidorDHCP.class.getName() ).log( Level.SEVERE, null, ex );
      }
    }

    @Override
    public void run()
    {
      while ( this.sistema.isEnviar() )
      {
        try
        {
          this.sistema.manejarSolicitud();
        }
        catch ( IOException ex )
        {
          Logger.getLogger( GUIServidorDHCP.class.getName() ).log( Level.SEVERE, null, ex );
        }
      }
    }

  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings( "unchecked" )
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents()
  {

    jLabel11 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    jTableIpAsignadas = new javax.swing.JTable();
    jButton1 = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    jLabel11.setFont(new java.awt.Font("Comic Sans MS", 3, 24)); // NOI18N
    jLabel11.setForeground(new java.awt.Color(0, 102, 0));
    jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel11.setText("Estado Actual IP's Asignadas");

    jTableIpAsignadas.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][]
      {
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null}
      },
      new String []
      {
        "Title 1", "Title 2", "Title 3", "Title 4"
      }
    ));
    jScrollPane1.setViewportView(jTableIpAsignadas);

    jButton1.setText("GENERAR ARCHIVO LOG");
    jButton1.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        jButton1ActionPerformed(evt);
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
            .addComponent(jScrollPane1))
          .addGroup(layout.createSequentialGroup()
            .addGap(100, 100, 100)
            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 122, Short.MAX_VALUE)))
        .addContainerGap())
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addGap(0, 0, Short.MAX_VALUE)
        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(191, 191, 191))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(29, 29, 29)
        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(38, 38, 38)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(18, 18, 18)
        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(26, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
  {//GEN-HEADEREND:event_jButton1ActionPerformed
    ManejoArchivos.generarLog( env.sistema );
  }//GEN-LAST:event_jButton1ActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main( String args[] )
  {
    /* Set the Nimbus look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try
    {
      for ( javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels() )
      {
        if ( "Nimbus".equals( info.getName() ) )
        {
          javax.swing.UIManager.setLookAndFeel( info.getClassName() );
          break;
        }
      }
    }
    catch ( ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex )
    {
      java.util.logging.Logger.getLogger( GUIServidorDHCP.class.getName() ).log( java.util.logging.Level.SEVERE, null, ex );
    }
    //</editor-fold>

    //</editor-fold>
    /* Create and display the form */
    java.awt.EventQueue.invokeLater( new Runnable()
    {

      public void run()
      {
        try
        {
          new GUIServidorDHCP().setVisible( true );
          GUIServidorDHCP.env.start();
          GUIServidorDHCP.refr.start();
        }
        catch ( UnknownHostException ex )
        {
          Logger.getLogger( GUIServidorDHCP.class.getName() ).log( Level.SEVERE, null, ex );
        }
        catch ( IOException ex )
        {
          Logger.getLogger( GUIServidorDHCP.class.getName() ).log( Level.SEVERE, null, ex );
        }
      }

    } );
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButton1;
  private javax.swing.JLabel jLabel11;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTable jTableIpAsignadas;
  // End of variables declaration//GEN-END:variables
  private void refrescarTabla()
  {
    while ( env.sistema.isModificando() );

    env.sistema.setModificando( true );
    Set<InetAddress> llavesRed = env.sistema.getSubRedes().keySet();
    Red subRed;
    int i = 0;
    Object[][] data = new Object[env.sistema.getIpAisg()][6];

    for ( InetAddress dirSubRed : llavesRed )
    {
      subRed = env.sistema.getSubRedes().get( dirSubRed );
      Set<InetAddress> llavesAsig = subRed.getIpsAsignadas().keySet();
      for ( InetAddress ipAsignada : llavesAsig )
      {

        System.out.println( "NUM: " + env.sistema.getIpAisg() + "    " + llavesAsig.toString() );
        Asignacion asig = subRed.getIpsAsignadas().get( ipAsignada );
        String macStr = "";
        byte[] mac = asig.getMac();
        for ( int j = 0; j < 6; j++ )
        {
          macStr += Integer.toHexString( mac[j] & 0xff ) + ( j < 5 ? ":" : " " );
        }
        data[i][0] = i+1;
        data[i][1] = asig.getIp().getHostAddress().substring( 0 );
        data[i][2] = macStr;
        data[i][3] = asig.getEstado();
        data[i][4] = asig.getHoraInicio();
        data[i][5] = asig.getHoraFin();
        i++;
      }
    }
    DefaultTableModel model = new DefaultTableModel( data, columnsIpAsiganadas );
    this.jTableIpAsignadas.setModel( model );
    env.sistema.setModificando( false );
  }

}