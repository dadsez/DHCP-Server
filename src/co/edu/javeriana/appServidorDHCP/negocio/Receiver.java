/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.javeriana.appServidorDHCP.negocio;


import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author ORLANNDO ABAUNZA -- DAVID SUÁREZ.
 */
public class Receiver extends Thread
{

  /**
   * Puerto por donde se recibirán mensajes DHCP.
   */
  private DHCPSocket socketR;

  /**
   * Para detener y reanudar la recepción automática de mensajes DHCP.
   */
  private boolean recibir;

  /**
   * Sistema central.
   */
  private Sistema sistema;


  //----------------------------------------------------------------------------------------------------------------------------------------------
  /**
   * Consctructor de la clase.
   * @param nombre Nombre del hilo.
   * @param sistema Sistema central.
   */
  public Receiver( String nombre, Sistema sistema )
  {
    super( nombre );
    try
    {
      this.sistema = sistema;
      this.socketR = new DHCPSocket( 67 );
      this.recibir = true;
    }
    catch ( SocketException ex )
    {
      Logger.getLogger( Receiver.class.getName() ).log( Level.SEVERE, null, ex );
    }
  }

  //----------------------------------------------------------------------------------------------------------------------------------------------
  /**
   * Gestiona, si el hilo no esta pausado, la recepción automática de mensajes
   * DHCP notificando la recepción mediante el sistema.
   */
  @Override
  public void run()
  {
    while ( this.recibir )
    {
      MensajeDHCP inMensaje = new MensajeDHCP();
      this.socketR.receive( inMensaje );
      if ( !inMensaje.getListaOpciones().getOptionsTable().isEmpty() )
      {
        this.sistema.agregarSolicitud( inMensaje );
        System.out.println( "ENTRAA" );
      }
    }

  }

//----------------------------------------------------------------------------------------------------------------------------------------------
  /**
   * Detiene la recepcíon automática de mensajes DHCP.
   */
  public void detener()
  {
    this.recibir = false;
  }

  //----------------------------------------------------------------------------------------------------------------------------------------------
  /**
   * Reanuda la recepcíon automática de mensajes DHCP.
   */
  public void reanudar()
  {
    this.recibir = true;
  }

  void enviar( MensajeDHCP respuesta ) throws IOException
  {
    this.socketR.send( respuesta );
  }

}
