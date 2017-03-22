/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.javeriana.appServidorDHCP.negocio;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author David Suárez, Orlando Abaunza.
 */
public class Sistema
{

  private InetAddress ipServidor;
  private Map< InetAddress, Red> subRedes;
  private List<Asignacion> historial;
  private List<MensajeDHCP> solicitudes;
  private boolean modificando;
  private final Receiver receptor;
  private final Reviewer revisor;
  private boolean enviar;
  private int ipAisg;


  public Sistema() throws UnknownHostException
  {

    this.subRedes = new HashMap<InetAddress, Red>();
    this.historial = new ArrayList<Asignacion>();
    this.solicitudes = new ArrayList<MensajeDHCP>();
    this.modificando = false;
    this.enviar = true;
    this.ipAisg = 0;
    this.receptor = new Receiver( "Receptor", this );
    this.revisor = new Reviewer( "Revisor", 15, this );
    this.receptor.start();
    this.revisor.start();
  }

  public int getIpAisg()
  {
    return ipAisg;
  }

  public void setIpAisg( int ipAisg )
  {
    this.ipAisg = ipAisg;
  }

  public Map<InetAddress, Red> getSubRedes()
  {
    return subRedes;
  }

  public void setSubRedes( Map<InetAddress, Red> subRedes )
  {
    this.subRedes = subRedes;
  }

  public List<Asignacion> getHistorial()
  {
    return historial;
  }

  public void setHistorial( List<Asignacion> historial )
  {
    this.historial = historial;
  }

  public List<MensajeDHCP> getSolicitudes()
  {
    return solicitudes;
  }

  public void setSolicitudes( List<MensajeDHCP> solicitudes )
  {
    this.solicitudes = solicitudes;
  }

  public boolean isModificando()
  {
    return modificando;
  }

  public void setModificando( boolean modificando )
  {
    this.modificando = modificando;
  }

  public InetAddress getIpServidor()
  {
    return ipServidor;
  }

  public void setIpServidor( InetAddress ipServidor )
  {
    this.ipServidor = ipServidor;
  }

  public boolean isEnviar()
  {
    return enviar;
  }

  public void setEnviar( boolean enviar )
  {
    this.enviar = enviar;
  }


  //----------------------------------------------------------------------------------------------------------------------------------------------
  /**
   * Detiene el envio automático de mensajes DHCP.
   */
  public void detener()
  {
    this.enviar = false;
  }

  //----------------------------------------------------------------------------------------------------------------------------------------------
  /**
   * Reanuda el envio automático de mensajes DHCP.
   */
  public void reanudar()
  {
    this.enviar = true;
  }


  void agregarSolicitud( MensajeDHCP inMensaje )
  {
    while ( this.modificando );
    this.modificando = true;
    this.solicitudes.add( inMensaje );
    this.modificando = false;
  }

  public void manejarSolicitud() throws UnknownHostException, IOException
  {
    byte[] tipoMsjDHCP;
    MensajeDHCP solicitud;
    Red subRed;
    InetAddress dirRouter;
    MensajeDHCP respuesta;

    while ( this.enviar )
    {
      while ( this.solicitudes.size() <= 0 || this.modificando );
      this.modificando = true;
      solicitud = this.solicitudes.get( 0 );
      this.solicitudes.remove( 0 );
      this.modificando = false;
      System.out.println( "ESCUCHEEEE" );
      dirRouter = InetAddress.getByAddress( solicitud.getGiaddr() );
      if ( this.subRedes.containsKey( dirRouter ) )
      {
        subRed = this.subRedes.get( dirRouter );
        respuesta = this.llenarMensajeDHCP( solicitud );

        tipoMsjDHCP = solicitud.getOpcion( 53 );
        System.out.println( "ID MENSAJEE: " + ( tipoMsjDHCP[0] & 0xff ) );
        if ( Byte.compare( tipoMsjDHCP[0], MensajeDHCP.DHCPDISCOVER ) == 0 )
        {
          if ( this.generarOffer( solicitud, respuesta, subRed ) )
          {
            Asignacion asig = new Asignacion( InetAddress.getByAddress( respuesta.getYiaddr() ), respuesta.getChaddr(), Asignacion.EN_OFERTA );
            subRed.getIpsAsignadas().put( asig.getIp(), asig );
            this.ipAisg++;
          }
          this.receptor.enviar( respuesta );
        }
        else if ( Byte.compare( tipoMsjDHCP[0], MensajeDHCP.DHCPREQUEST ) == 0 )
        {
          InetAddress local = InetAddress.getByName( "0.0.0.0" );
          System.out.println( "AJJAAJJA" );
          InetAddress ciaddr = InetAddress.getByAddress( solicitud.getCiaddr() );

          if ( solicitud.existeOpcion( DHCPOptions.OPTION_DHCP_SERVER_IDENTIFIER ) )
          {
            InetAddress ipServSolicitado = InetAddress.getByAddress( solicitud.getOpcion( DHCPOptions.OPTION_DHCP_SERVER_IDENTIFIER ) );
            if ( this.ipServidor.equals( ipServSolicitado ) )
            {
              if ( this.generarAck( solicitud, respuesta, subRed ) )
              {
                InetAddress ipAsig = InetAddress.getByAddress( respuesta.getYiaddr() );
                if ( !subRed.getIpsAsignadas().containsKey( ipAsig ) )
                {
                  Asignacion asig = new Asignacion( InetAddress.getByAddress( respuesta.getYiaddr() ), respuesta.getChaddr(), Asignacion.EN_OFERTA );
                  subRed.getIpsAsignadas().put( asig.getIp(), asig );
                  this.ipAisg++;
                }
                subRed.getIpsAsignadas().get( ipAsig ).asignar();
                subRed.getIpsAsignadas().get( InetAddress.getByAddress( respuesta.getYiaddr() ) ).setMac( respuesta.getChaddr() );
              }
            }
            else
            {
              InetAddress ipOfertada = InetAddress.getByAddress( solicitud.getOpcion( DHCPOptions.OPTION_DHCP_IP_ADRESS_REQUESTED ) );
              subRed.getIpsAsignadas().remove( ipOfertada );
              this.ipAisg--;
            }
          }
          else if ( ciaddr.equals( local ) )
          {
            if ( this.generarAck( solicitud, respuesta, subRed ) )
            {
              InetAddress ipAsig = InetAddress.getByAddress( respuesta.getYiaddr() );
              if ( !subRed.getIpsAsignadas().containsKey( ipAsig ) )
              {
                Asignacion asig = new Asignacion( InetAddress.getByAddress( respuesta.getYiaddr() ), respuesta.getChaddr(), Asignacion.EN_OFERTA );
                subRed.getIpsAsignadas().put( asig.getIp(), asig );
                this.ipAisg++;
              }
              subRed.getIpsAsignadas().get( ipAsig ).asignar();
              subRed.getIpsAsignadas().get( ipAsig ).setMac( respuesta.getChaddr() );
            }
          }
          else
          {
            this.renovar( respuesta, subRed );
          }
          this.receptor.enviar( respuesta );
        }
        else if ( Byte.compare( tipoMsjDHCP[0], MensajeDHCP.DHCPRELEASE ) == 0 )
        {
          InetAddress ipALiberar = InetAddress.getByAddress( solicitud.getCiaddr() );
          this.liberarIP( ipALiberar, subRed, Asignacion.LIBERADA );
        }
      }
      System.out.println( this.subRedes.toString() );
    }
  }


  private boolean generarOffer( MensajeDHCP solicitud, MensajeDHCP resp, Red subRed ) throws UnknownHostException, IOException
  {
    InetAddress ipDeAsigancion = InetAddress.getByAddress( solicitud.getYiaddr() );
    ByteArrayOutputStream flujoBytes;
    DataOutputStream msjEnBytes;
    boolean asigno = false;


    // Verifica si el cliente esta solicitado una ip antes asiganda.
    if ( solicitud.existeOpcion( DHCPOptions.OPTION_DHCP_IP_ADRESS_REQUESTED ) )
    {
      ipDeAsigancion = InetAddress.getByAddress( solicitud.getOpcion( DHCPOptions.OPTION_DHCP_IP_ADRESS_REQUESTED ) );
      if ( !subRed.estaAsignada( ipDeAsigancion ) && !subRed.estaEnOferta( ipDeAsigancion ) )
      {
        asigno = true;
      }
    }

    if ( !asigno )
    {
      ipDeAsigancion = subRed.generarIp( ipDeAsigancion );
    }

    resp.setYiaddr( ipDeAsigancion.getAddress() ); //Ip ofertada al cliente.
    resp.setOption( DHCPOptions.OPTION_NETMASK, subRed.getMascaraRed().getAddress() ); //Mascara de red asociada.
    System.out.println( "GENERANDO OFFER\n" );
    //Tiempo que durará el arrendamiento de la ip.
    flujoBytes = new ByteArrayOutputStream();
    msjEnBytes = new DataOutputStream( flujoBytes );
    msjEnBytes.writeInt( subRed.getTimpoLimite() );
    byte tiempo[] = flujoBytes.toByteArray();
    resp.setOption( DHCPOptions.OPTION_DHCP_IP_LEASE_TIME, tiempo );

    //Tipo de mensaje OFFER
    byte[] tipoMensaje = new byte[1];
    tipoMensaje[0] = MensajeDHCP.DHCPOFFER;
    resp.setOption( DHCPOptions.OPTION_DHCP_MESSAGE_TYPE, tipoMensaje );

    //Tiempo en que el cliente pedira renovación de contrato.
    flujoBytes = new ByteArrayOutputStream();
    msjEnBytes = new DataOutputStream( flujoBytes );
    int tiempoRen = subRed.getTimpoLimite() / 2;
    msjEnBytes.writeInt( tiempoRen );
    tiempo = flujoBytes.toByteArray();
    resp.setOption( DHCPOptions.OPTION_DHCP_RENEWAL_TIME, tiempo );
    asigno = true;
    
    return asigno;
  }

  private boolean generarAck( MensajeDHCP solicitud, MensajeDHCP resp, Red subRed ) throws UnknownHostException, IOException
  {
    InetAddress ipDeAsigancion = InetAddress.getByAddress( solicitud.getYiaddr() );
    ByteArrayOutputStream flujoBytes;
    DataOutputStream msjEnBytes;
    boolean asigno = false;

    if ( solicitud.existeOpcion( DHCPOptions.OPTION_DHCP_IP_ADRESS_REQUESTED ) )
    {
      ipDeAsigancion = InetAddress.getByAddress( solicitud.getOpcion( DHCPOptions.OPTION_DHCP_IP_ADRESS_REQUESTED ) );
      if ( subRed.estaAsignada( ipDeAsigancion ) )
      {
        byte[] tipoMensaje = new byte[1];
        tipoMensaje[0] = MensajeDHCP.DHCPNAK;
        resp.setOption( DHCPOptions.OPTION_DHCP_MESSAGE_TYPE, tipoMensaje );
        System.out.println( "GENERANDO ACNK\n" );
      }
      else
      {
        resp.setYiaddr( ipDeAsigancion.getAddress() ); //Ip ofertada al cliente.
        resp.setOption( DHCPOptions.OPTION_NETMASK, subRed.getMascaraRed().getAddress() ); //Mascara de red asociada.
        System.out.println( "GENERANDO ACK\n" );
        //Tiempo que durará el arrendamiento de la ip.
        flujoBytes = new ByteArrayOutputStream();
        msjEnBytes = new DataOutputStream( flujoBytes );
        msjEnBytes.writeInt( subRed.getTimpoLimite() );
        byte tiempo[] = flujoBytes.toByteArray();
        resp.setOption( DHCPOptions.OPTION_DHCP_IP_LEASE_TIME, tiempo );

        //Tipo de mensaje ACK
        byte[] tipoMensaje = new byte[1];
        tipoMensaje[0] = MensajeDHCP.DHCPACK;
        resp.setOption( DHCPOptions.OPTION_DHCP_MESSAGE_TYPE, tipoMensaje );


        //Tiempo en que el cliente pedira renovación de contrato.
        flujoBytes = new ByteArrayOutputStream();
        msjEnBytes = new DataOutputStream( flujoBytes );
        int tiempoRen = subRed.getTimpoLimite() / 2;
        msjEnBytes.writeInt( tiempoRen );
        byte tiempoRenB[] = flujoBytes.toByteArray();
        resp.setOption( DHCPOptions.OPTION_DHCP_RENEWAL_TIME, tiempoRenB );
        asigno = true;
      }
    }
    return asigno;
  }

  private MensajeDHCP llenarMensajeDHCP( MensajeDHCP solicitud ) throws UnknownHostException
  {
    MensajeDHCP respuesta = new MensajeDHCP();
    InetAddress ipDestino = this.seleccionarDestino( solicitud );

    /*Construyendo mensaje DHCP*/
    respuesta.setOpcion( MensajeDHCP.REPLY );           //Opcion respuesta.
    respuesta.setTipoMAC( solicitud.getTipoMAC() );      //Tipo dirección física (MAC).
    respuesta.setTamMac( solicitud.getTamMac() );        //Tamaño dirección física (MAC).
    respuesta.setHops( solicitud.getHops() );
    respuesta.setIdTrans( solicitud.getIdTrans() );      //Identificador único que maneja cliente y servidor.
    respuesta.setSecs( solicitud.getSecs() );            //Tiempo transcurrido (en segundos) desde la petición de la IP.
    respuesta.setCiaddr( solicitud.getCiaddr() );
    respuesta.setSiaddr( this.ipServidor.getAddress() ); //Ip del servidor.
    respuesta.setFlags( solicitud.getFlags() );
    respuesta.setGiaddr( solicitud.getGiaddr() );        //Ip del router.
    respuesta.setChaddr( solicitud.getChaddr() );        //Direecion física del cliente (MAC).
    respuesta.setOption( DHCPOptions.OPTION_DHCP_SERVER_IDENTIFIER, this.ipServidor.getAddress() ); //Identificador del servidor (IP).
    respuesta.setArchivo( solicitud.getArchivo() );
    respuesta.setNomHost( solicitud.getNomHost() );
    respuesta.setIpDestino( ipDestino );

    return respuesta;
  }

  public void agregarRed( InetAddress dirRouter, InetAddress dirRed, InetAddress mascRed, int tmpLimite )
  {
    Red subRed = new Red( dirRed, mascRed, dirRouter, tmpLimite );
    this.subRedes.put( dirRouter, subRed );
  }

  private void renovar( MensajeDHCP respuesta, Red subRed ) throws IOException
  {
    ByteArrayOutputStream flujoBytes;
    DataOutputStream msjEnBytes;

    //Nuevo tiempo que durará el arrendamiento de la ip.
    flujoBytes = new ByteArrayOutputStream();
    msjEnBytes = new DataOutputStream( flujoBytes );
    msjEnBytes.writeInt( subRed.getTimpoLimite() );
    byte tiempo[] = flujoBytes.toByteArray();
    respuesta.setOption( DHCPOptions.OPTION_DHCP_IP_LEASE_TIME, tiempo );

    //Tipo de mensaje ACK
    byte[] tipoMensaje = new byte[1];
    tipoMensaje[0] = MensajeDHCP.DHCPACK;
    respuesta.setOption( DHCPOptions.OPTION_DHCP_MESSAGE_TYPE, tipoMensaje );


    //Tiempo en que el cliente pedira renovación de contrato.
    flujoBytes = new ByteArrayOutputStream();
    msjEnBytes = new DataOutputStream( flujoBytes );
    int tiempoRen = subRed.getTimpoLimite() / 2;
    msjEnBytes.writeInt( tiempoRen );
    byte tiempoRenB[] = flujoBytes.toByteArray();
    respuesta.setOption( DHCPOptions.OPTION_DHCP_RENEWAL_TIME, tiempoRenB );

    subRed.getIpsAsignadas().get( InetAddress.getByAddress( respuesta.getCiaddr() ) ).setAsignadaDesde( ( int ) System.currentTimeMillis() );
  }

  private InetAddress seleccionarDestino( MensajeDHCP solicitud ) throws UnknownHostException
  {
    InetAddress local = InetAddress.getByName( "0.0.0.0" );
    InetAddress giaddr = InetAddress.getByAddress( solicitud.getGiaddr() );
    InetAddress ciaddr = InetAddress.getByAddress( solicitud.getCiaddr() );

    if ( !ciaddr.equals( local ) )
    {
      return ciaddr;
    }
    if ( !giaddr.equals( local ) )
    {
      return giaddr;
    }

    return MensajeDHCP.BROADCAST_ADDR;
  }

  private void liberarIP( InetAddress ipALiberar, Red subRed, String estado ) throws UnknownHostException
  {
    while ( this.modificando );
    this.modificando = true;

    System.out.println( "RELEASE de: " + ipALiberar.toString() );
    Asignacion hist = subRed.getIpsAsignadas().remove( ipALiberar );
    System.out.println( "ANTES:" + hist.getEstado() );
    hist.liberar( estado );
    System.out.println( "DESPUES:" + hist.getEstado() );
    this.historial.add( hist );
    this.ipAisg--;
    this.modificando = false;
  }

  public void revisarAsignaciones() throws UnknownHostException
  {

    while ( this.modificando );

    this.modificando = true;
    Set<InetAddress> llavesRed = this.subRedes.keySet();
    Red subRed;
    for ( InetAddress dirSubRed : llavesRed )
    {
      subRed = this.subRedes.get( dirSubRed );
      Set<InetAddress> llavesAsig = subRed.getIpsAsignadas().keySet();
      for ( InetAddress ipAsignada : llavesAsig )
      {
        Asignacion asig = subRed.getIpsAsignadas().get( ipAsignada );
        if ( asig.revocar( ( int ) System.currentTimeMillis(), subRed.getTimpoLimite() ) )
        {
          this.liberarIP( asig.getIp(), subRed, Asignacion.REVOCADA );
        }
        if ( asig.getEstado().compareTo( Asignacion.EN_OFERTA ) == 0 )
        {
          subRed.getIpsAsignadas().remove( ipAsignada );
          this.ipAisg--;
        }
      }
    }
    this.modificando = false;
  }


}
