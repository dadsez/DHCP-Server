/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serrvidordhcp;


import co.edu.javeriana.appServidorDHCP.negocio.DHCPOptions;
import co.edu.javeriana.appServidorDHCP.negocio.MensajeDHCP;
import co.edu.javeriana.appServidorDHCP.negocio.DHCPSocket;
import co.edu.javeriana.appServidorDHCP.negocio.Red;
import co.edu.javeriana.appServidorDHCP.negocio.Sistema;
import co.edu.javeriana.appServidorDHCP.persistencia.ManejoArchivos;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author DAVID FELIPE
 */
public class SerrvidorDHCP
{

  /**
   * @param args the command line arguments
   */
  public static void main( String[] args ) 
  {

    try
    {
      Sistema sistema = new Sistema();
      ManejoArchivos.leerConfServidor( sistema );
      sistema.manejarSolicitud();
      /* DHCPSocket soc = new DHCPSocket( 67 );
      MensajeDHCP in = new MensajeDHCP();
      soc.receive( in );
      if ( !in.getListaOpciones().getOptionsTable().isEmpty() )
      {
      System.out.println( "SIIIIIII" );

      byte[] tipoM = in.getOpcion( 53 );
      System.out.println( "MENSAJEE: " + ( tipoM[0] & 0xff ) );
      if ( Byte.compare( tipoM[0], MensajeDHCP.DHCPREQUEST ) == 0 )
      {
      MensajeDHCP out = new MensajeDHCP( in );
      
      InetAddress yiadr = InetAddress.getByName( "192.168.0.4" );
      int seg = 180;
      ByteArrayOutputStream flujoBytes = new ByteArrayOutputStream();
      DataOutputStream msjEnBytes = new DataOutputStream( flujoBytes );
      msjEnBytes.writeInt( seg );
      byte datos[] = flujoBytes.toByteArray();
      
      out.setYiaddr( yiadr.getAddress() );
      out.setOption( 58, datos );
      yiadr = InetAddress.getByName( "192.168.0.10" );
      out.setOption( 54, yiadr.getAddress() );
      yiadr = InetAddress.getByName( "255.255.240.0" );
      out.setOption( 1, yiadr.getAddress() );
      flujoBytes = new ByteArrayOutputStream();
      msjEnBytes = new DataOutputStream( flujoBytes );
      msjEnBytes.writeByte( MensajeDHCP.DHCPACK );
      datos = flujoBytes.toByteArray();
      out.setOption( 53, datos );
      out.setIpDestinot( "255.255.255.255." );
      soc.send( out );
      }
      }*/
    }
    catch ( Exception ex )
    {
      Logger.getLogger( SerrvidorDHCP.class.getName() ).log( Level.SEVERE, null, ex );
    }
  }

}
