package co.edu.javeriana.appServidorDHCP.negocio;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Representa un mensaje DHCP báscio.
 *
 * @author David Suárez, Orlando Abaunza.
 */
public class MensajeDHCP
{

  // ------------------------Constantes ------------------------

  /**
   * Identicador de una operación request.
   */
  public static final byte REQUEST = 1;

  /**
   * Identicador de una operación reply.
   */
  public static final byte REPLY = 2;

  /**
   * Representa tipo de mensaje DHCPDISCOVER.
   */
  public static final byte DHCPDISCOVER = 1;

  /**
   * Representa tipo de mensaje DHCPOFFER.
   */
  public static final byte DHCPOFFER = 2;

  /**
   * Representa tipo de mensaje DHCPREQUEST.
   */
  public static final byte DHCPREQUEST = 3;

  /**
   * Representa tipo de mensaje DHCPDECLINE.
   */
  public static final byte DHCPDECLINE = 4;

  /**
   * Representa tipo de mensaje DHCPACK.
   */
  public static final byte DHCPACK = 5;

  /**
   * Representa tipo de mensaje DHCPNAK.
   */
  public static final byte DHCPNAK = 6;

  /**
   * Representa tipo de mensaje DHCPRELEASE.
   */
  public static final byte DHCPRELEASE = 7;

  /**
   * Default DHCP client port
   */
  public static final int PUERTO_CLIENTE = 68;

  /**
   * Puerto por defecto por donde el servidor DHCP envía y recibe mensajes DHCP.
   */
  public static final int PUERTO_SERVIDOR = 67;

  /**
   * Broadcast Adress to send packets to
   */
  public static InetAddress BROADCAST_ADDR = null;


  //---------------Fields defining a dhcp message---------------
  /**
   * Tipo de operación del mensaje (REPLY o REQUEST).
   */
  private byte opcion;

  /**
   * Tipo de direción física.
   */
  private byte tipoMAC;

  /**
   * Tamaño de la direción física.
   */
  private byte tamMac;

  /**
   *
   */
  private byte hops;

  /**
   * Identificador del mensaje DHCP entre cliente y servidor.
   */
  private int idTrans;

  /**
   * Segundos transcurridos desde un petición del cliente.
   */
  private short secs;

  /**
   * Bit para definir si se responde en sentido broadcast.
   */
  private short flags;

  /**
   * Dirección IP del cliente, campo usado cuando un cliente pide una dirección
   * asiganada anteriormente.
   */
  private byte ciaddr[] = new byte[4];

  /**
   * Dirección IP a asignar.
   */
  private byte yiaddr[] = new byte[4];

  /**
   * Dirección IP del servidor que esta majenado DHCP.
   */
  private byte siaddr[] = new byte[4];

  /**
   * Dirección del router, campo usado cuando el cliente pertenece a una subred
   * distinta de la del servidor.
   */
  private byte giaddr[] = new byte[4];

  /**
   * Dirección MAC del cliente.
   */
  private byte chaddr[] = new byte[16];

  /**
   * Nombre del Hosts.
   */
  private byte[] nomHost = new byte[64];

  /**
   * Archivo con configuraciones de BOOT.
   */
  private byte[] archivo = new byte[128];

  /**
   * Internal representation of the given DHCP options.
   */
  private DHCPOptions ListaOpciones = null;

  /**
   * Puerto por donde se enviará el mensaje DHCP.
   */
  private int puerto;

  /**
   * La dirección IP de destino del mensaje DHCP.
   */
  private InetAddress ipDestino;

  static
  {
    try
    {
      BROADCAST_ADDR = InetAddress.getByName( "255.255.255.255" );
    }
    catch ( UnknownHostException e )
    {
    }
  }

  // -----------------------Constructores------------------------	
  /**
   * Creates empty DHCPMessage object, initializes the object, sets the host to
   * the broadcast address, the local subnet, binds to the default server port.
   */
  public MensajeDHCP()
  {
    iniciar();
    ipDestino = BROADCAST_ADDR;
    puerto = PUERTO_CLIENTE;
  }

  /**
   * Constructor por copia. Crea una instancia de un MensajeDHCP de una copia de
   * otro.
   *
   * @param otro The message to be copied
   */
  public MensajeDHCP( MensajeDHCP otro )
  {
    iniciar();

    ipDestino = BROADCAST_ADDR;
    puerto = PUERTO_CLIENTE;
    opcion = otro.getOpcion();
    tipoMAC = otro.getTipoMAC();
    tamMac = otro.getTamMac();
    hops = otro.getHops();
    idTrans = otro.getIdTrans();
    secs = otro.getSecs();
    flags = otro.getFlags();
    ciaddr = otro.getCiaddr();
    yiaddr = otro.getYiaddr();
    siaddr = otro.getSiaddr();
    giaddr = otro.getGiaddr();
    chaddr = otro.getChaddr();
    nomHost = otro.getNomHost();
    archivo = otro.getArchivo();
    ListaOpciones.internalize( otro.getOptions() );
  }

  // ---------------------------Metodos-------------------------

  /**
   * Inicializa la lista de opciones del mensaje.
   */
  private void iniciar()
  {
    ListaOpciones = new DHCPOptions();
  }

  /**
   * Convierte a MensajeDHCP en un flujo de Bytes.
   *
   * @return Un arreglo de Bytes con el mensaje DHCP.
   */
  public synchronized byte[] DCHPaBytes()
  {
    ByteArrayOutputStream flujoBytes = new ByteArrayOutputStream();
    DataOutputStream msjEnBytes = new DataOutputStream( flujoBytes );

    try
    {
      msjEnBytes.writeByte( opcion );
      msjEnBytes.writeByte( tipoMAC );
      msjEnBytes.writeByte( tamMac );
      msjEnBytes.writeByte( hops );
      msjEnBytes.writeInt( idTrans );
      msjEnBytes.writeShort( secs );
      msjEnBytes.writeShort( flags );
      msjEnBytes.write( ciaddr, 0, 4 );
      msjEnBytes.write( yiaddr, 0, 4 );
      msjEnBytes.write( siaddr, 0, 4 );
      msjEnBytes.write( giaddr, 0, 4 );
      msjEnBytes.write( chaddr, 0, 16 );
      msjEnBytes.write( nomHost, 0, 64 );
      msjEnBytes.write( archivo, 0, 128 );

      byte[] opciones = new byte[312];
      if ( ListaOpciones == null )
      {
        iniciar();
      }

      opciones = ListaOpciones.externalize();
      msjEnBytes.write( opciones, 0, 312 );
    }
    catch ( IOException e )
    {
      System.err.println( e );
    }

    byte datos[] = flujoBytes.toByteArray();

    return datos;
  }

  /**
   * Convierte un flujo de Bytes en un MensajeDHCP.
   *
   * @param datosBytes Byte array to convert to a MensajeDHCP object
   * @return A MensajeDHCP object with information from byte array.
   */

  public synchronized MensajeDHCP BytesADHCP( byte[] datosBytes )
  {
    ByteArrayInputStream flujoBytes
            = new ByteArrayInputStream( datosBytes, 0, datosBytes.length );
    DataInputStream flujoEnt = new DataInputStream( flujoBytes );

    try
    {
      opcion = flujoEnt.readByte();
      tipoMAC = flujoEnt.readByte();
      tamMac = flujoEnt.readByte();
      hops = flujoEnt.readByte();
      idTrans = flujoEnt.readInt();
      secs = flujoEnt.readShort();
      flags = flujoEnt.readShort();
      flujoEnt.readFully( ciaddr, 0, 4 );
      flujoEnt.readFully( yiaddr, 0, 4 );
      flujoEnt.readFully( siaddr, 0, 4 );
      flujoEnt.readFully( giaddr, 0, 4 );
      flujoEnt.readFully( chaddr, 0, 16 );
      flujoEnt.readFully( nomHost, 0, 64 );
      flujoEnt.readFully( archivo, 0, 128 );

      byte[] opciones = new byte[312];
      flujoEnt.readFully( opciones, 0, 312 );
      if ( ListaOpciones == null )
      {
        iniciar();
      }

      ListaOpciones.internalize( opciones );
    }
    catch ( IOException e )
    {
      System.err.println( e );
    }
    return this;
  }

  /**
   * Set message Op code / message type.
   *
   * @param opcion message Op code / message type
   */
  public void setOpcion( byte opcion )
  {
    this.opcion = opcion;
  }

  /**
   * Set hardware address type.
   *
   * @param tipoMAC hardware address type
   */
  public void setTipoMAC( byte tipoMAC )
  {
    this.tipoMAC = tipoMAC;
  }

  /**
   * Set hardware address length.
   *
   * @param tamMac hardware address length
   */
  public void setTamMac( byte tamMac )
  {
    this.tamMac = tamMac;
  }

  /**
   * Set hops field.
   *
   * @param inHops hops field
   */
  public void setHops( byte inHops )
  {
    this.hops = inHops;
  }

  /**
   * Set transaction ID.
   *
   * @param inIdTans idTrans transactionID
   */
  public void setIdTrans( int inIdTans )
  {
    this.idTrans = inIdTans;
  }

  /**
   * Set seconds elapsed since client began address acquisition or renewal
   * process.
   *
   * @param inSecs Seconds elapsed since client began address acquisition or
   * renewal process
   */
  public void setSecs( short inSecs )
  {
    secs = inSecs;
  }

  /**
   * Set flags field.
   *
   * @param inFlags flags field
   */
  public void setFlags( short inFlags )
  {
    flags = inFlags;
  }

  /**
   * Set client IP address.
   *
   * @param inCiaddr client IP address
   */
  public void setCiaddr( byte[] inCiaddr )
  {
    ciaddr = inCiaddr;
  }

  /**
   * Set 'your' (client) IP address.
   *
   * @param inYiaddr 'your' (client) IP address
   */
  public void setYiaddr( byte[] inYiaddr )
  {
    yiaddr = inYiaddr;
  }

  /**
   * Set address of next server to use in bootstrap.
   *
   * @param inSiaddr address of next server to use in bootstrap
   */
  public void setSiaddr( byte[] inSiaddr )
  {
    siaddr = inSiaddr;
  }

  /**
   * Set relay agent IP address.
   *
   * @param inGiaddr relay agent IP address
   */
  public void setGiaddr( byte[] inGiaddr )
  {
    giaddr = inGiaddr;
  }
  public DHCPOptions getListaOpciones()
  {
    return ListaOpciones;
  }

  public void setListaOpciones( DHCPOptions ListaOpciones )
  {
    this.ListaOpciones = ListaOpciones;
  }
  public InetAddress getIpDestino()
  {
    return ipDestino;
  }

  public void setIpDestino( InetAddress ipDestino )
  {
    this.ipDestino = ipDestino;
  }

  /**
   * Set client harware address.
   *
   * @param inChaddr client hardware address
   */
  public void setChaddr( byte[] inChaddr )
  {
    this.chaddr = inChaddr;
  }

  /**
   * Set optional server host name.
   *
   * @param nomHost server host name
   */
  public void setNomHost( byte[] nomHost )
  {
    this.nomHost = nomHost;
  }

  /**
   * Set boot archivo name.
   *
   * @param archivo boot archivo name
   */
  public void setArchivo( byte[] archivo )
  {
    this.archivo = archivo;
  }

  /**
   * Set message destination port.
   *
   * @param puerto port on message destination host
   */
  public void setPuerto( int puerto )
  {
    this.puerto = puerto;
  }

  /**
   * Set message destination IP
   * @param ipDestino string representation of message destination IP or
   * hostname
   */
  public void setIpDestinot( String ipDestino )
  {
    try
    {
      this.ipDestino = InetAddress.getByName( ipDestino );
    }
    catch ( Exception e )
    {
      System.out.println( "AQIIII" );
      System.err.println( e );
    }
  }

  /**
   * @return message Op code / message type.
   */
  public byte getOpcion()
  {
    return opcion;
  }

  /**
   * @return hardware address type.
   */
  public byte getTipoMAC()
  {
    return tipoMAC;
  }

  /**
   * @return hardware address length.
   */
  public byte getTamMac()
  {
    return tamMac;
  }

  /**
   * @return hops field.
   */
  public byte getHops()
  {
    return hops;
  }

  /**
   * @return transaction ID.
   */
  public int getIdTrans()
  {
    return idTrans;
  }

  /**
   * @return seconds elapsed since client began address acquisition or renewal
   * process.
   */
  public short getSecs()
  {
    return secs;
  }

  /**
   * @return flags field.
   */
  public short getFlags()
  {
    return flags;
  }

  /**
   * @return client IP address.
   */
  public byte[] getCiaddr()
  {
    return ciaddr;
  }

  /**
   * @return 'your' (client) IP address.
   */
  public byte[] getYiaddr()
  {
    return yiaddr;
  }

  /**
   * @return address of next server to use in bootstrap.
   */
  public byte[] getSiaddr()
  {
    return siaddr;
  }

  /**
   * @return relay agent IP address.
   */
  public byte[] getGiaddr()
  {
    return giaddr;
  }

  /**
   * @return client harware address.
   */
  public byte[] getChaddr()
  {
    return chaddr;
  }

  /**
   * @return optional server host name.
   */
  public byte[] getNomHost()
  {
    return nomHost;
  }

  /**
   * @return boot archivo name.
   */
  public byte[] getArchivo()
  {
    return archivo;
  }

  /**
   * @return a byte array containing options
   */
  public byte[] getOptions()
  {
    if ( ListaOpciones == null )
    {
      iniciar();
    }
    return ListaOpciones.externalize();
  }

  /**
   * @return An interger representation of the message destination port
   */
  public int getPort()
  {
    return puerto;
  }

  /**
   * Get message destination hostname
   *
   * @return A string representing the hostname of the message destination
   * server
   */
  public String getDestinationAddress()
  {
    return ipDestino.getHostAddress();
  }

  /**
   * Sets DHCP options in MensajeDHCP. If option already exists then remove old
   * option and insert a new one.
   *
   * @param inOptNum option number
   * @param inOptionData option data
   */
  public void setOption( int inOptNum, byte[] inOptionData )
  {
    ListaOpciones.setOption( ( byte ) inOptNum, inOptionData );
  }

  /**
   * Retorna los datos de la opción indicada por opcion. Null es retornado si la
   * opcioón no ha sido asignada.
   *
   * @param opcion Número de Opción
   *
   * @return Datos de la opción.
   */
  public byte[] getOpcion( int opcion )
  {
    if ( ListaOpciones == null )
    {
      iniciar();
    }
    return ListaOpciones.getOption( ( byte ) opcion );
  }

  /**
   * Eliminalos datos de la opción indicada por opcion.
   *
   * @param opcion Número de Opción.
   */
  public void eliminarOpcion( int opcion )
  {
    if ( ListaOpciones == null )
    {
      iniciar();
    }
    ListaOpciones.removeOption( ( byte ) opcion );
  }

  /**
   * Informa si una opcion dada por opcion esta asignada en el mensaje.
   *
   * @param opcion Número de Opción.
   *
   * @return TRUE si existe, FALSE de o contrario.
   */
  public boolean existeOpcion( int opcion )
  {
    if ( ListaOpciones == null )
    {
      iniciar();
    }

    return ListaOpciones.contains( ( byte ) opcion );
  }

}
