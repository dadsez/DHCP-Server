/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.javeriana.appServidorDHCP.negocio;


import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * @author DAVID FELIPE
 */
public class Asignacion
{

  public static final String EN_OFERTA = "EN_OFERTA";
  public static final String ASIGNADA = "ASIGNADA";
  public static final String LIBERADA = "LIBERADA";
  public static final String REVOCADA = "REVOCADA";

  private InetAddress ip;
  private byte[] mac;
  private String estado;
  private String horaInicio;
  private String horaFin;
  private int asignadaDesde;


  public Asignacion( InetAddress ip, byte[] mac, String estado )
  {
    this.ip = ip;
    this.mac = mac;
    this.estado = estado;
    this.horaFin = "IP EN OFERTA";
  }

  public InetAddress getIp()
  {
    return ip;
  }

  public void setIp( InetAddress ip )
  {
    this.ip = ip;
  }

  public byte[] getMac()
  {
    return mac;
  }

  public void setMac( byte[] mac )
  {
    this.mac = mac;
  }

  public String getEstado()
  {
    return estado;
  }

  public void setEstado( String estado )
  {
    this.estado = estado;
  }

  public String getHoraInicio()
  {
    return horaInicio;
  }

  public void setHoraInicio( String horaInicio )
  {
    this.horaInicio = horaInicio;
  }

  public String getHoraFin()
  {
    return horaFin;
  }

  public void setHoraFin( String horaFin )
  {
    this.horaFin = horaFin;
  }

  public int getAsignadaDesde()
  {
    return asignadaDesde;
  }

  public void setAsignadaDesde( int asignadaDesde )
  {
    this.asignadaDesde = asignadaDesde;
  }

  public void asignar()
  {
    Date now = new Date( System.currentTimeMillis() );
    SimpleDateFormat date = new SimpleDateFormat( "yyyy-MM-dd" );
    SimpleDateFormat hour = new SimpleDateFormat( "HH:mm:ss" );
    date.format( now );
    hour.format( now );

    this.estado = Asignacion.ASIGNADA;
    this.horaInicio = now.toString();
    this.horaFin = "IP asignada actualmente";
    this.asignadaDesde = ( int ) System.currentTimeMillis();
  }


  public void liberar( String estado )
  {
    Date now = new Date( System.currentTimeMillis() );
    SimpleDateFormat date = new SimpleDateFormat( "yyyy-MM-dd" );
    SimpleDateFormat hour = new SimpleDateFormat( "HH:mm:ss" );
    date.format( now );
    hour.format( now );

    this.estado = estado;
    this.horaFin = now.toString();
  }


  public boolean revocar( int tiempoActual, int timpoLimite )
  {
    return ( tiempoActual - this.asignadaDesde ) / 1000 >= timpoLimite;
  }

  @Override
  public String toString()
  {
    return "" + ip + " --- " + mac + " --- " + estado + " --- " + horaInicio + " --- " + horaFin + "\n";
  }

}
