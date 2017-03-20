package co.edu.javeriana.appServidorDHCP.negocio;


import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author ORLANDO ABAUNZA -- DAVID SUÁREZ
 */
public class Reviewer extends Thread
{

  /**
   * Periodicidad de revisión.
   */
  private int segEspera;

  /**
   * Sistema central.
   */
  private final Sistema sistema;
  private boolean correr;

  //----------------------------------------------------------------------------------------------------------------------------------------------
  /**
   * Constructor de la clase.
   * @param nombre Nombre del hilo.
   * @param segEspera Periodicidad de revisión.
   * @param sistema Sistema central.
   */
  public Reviewer( String nombre, int segEspera, Sistema sistema )
  {
    super( nombre );
    this.segEspera = segEspera*1000;
    this.sistema = sistema;
    this.correr = true;
  }

  //----------------------------------------------------------------------------------------------------------------------------------------------
  public int getSegEspera()
  {
    return segEspera;
  }

  //----------------------------------------------------------------------------------------------------------------------------------------------
  public void setSegEspera( int segEspera )
  {
    this.segEspera = segEspera;
  }

  //----------------------------------------------------------------------------------------------------------------------------------------------
  /**
   * Revisa periodicamente el estado de los dispositivos, para cambiarlos
   * dependendo si han respondido o no una cantidad de tramas ARP reply.
   */
  @Override
  public void run()
  {
    while ( this.correr )
    {
      try
      {
        Thread.sleep( segEspera );
        this.sistema.revisarAsignaciones();
      }
      catch ( InterruptedException | UnknownHostException ex )
      {
        Logger.getLogger( Reviewer.class.getName() ).log( Level.SEVERE, null, ex );
      }
    }
  }
  
    //----------------------------------------------------------------------------------------------------------------------------------------------  
  /**
   * Acaba la recepción automático de tramas ARP.
   */
  public void matar()
  {
    this.correr = false;
  }

}

