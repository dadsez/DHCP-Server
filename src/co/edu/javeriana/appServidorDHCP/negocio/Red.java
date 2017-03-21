/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.javeriana.appServidorDHCP.negocio;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author DAVID FELIPE
 */
public class Red
{

  static final int BROADCAST = 255;

  private InetAddress dirRouter;
  private InetAddress dirRed;
  private InetAddress mascaraRed;
  private int timpoLimite;
  private Map< InetAddress, Asignacion> ipsAsignadas;

  public Red( InetAddress dirRed, InetAddress mascaraRed, InetAddress dirRouter, int timpoLimite )
  {
    this.dirRed = dirRed;
    this.mascaraRed = mascaraRed;
    this.dirRouter = dirRouter;
    this.timpoLimite = timpoLimite;
    this.ipsAsignadas = new HashMap<InetAddress, Asignacion>();
  }

  public InetAddress getDirRed()
  {
    return dirRed;
  }

  public void setDirRed( InetAddress dirRed )
  {
    this.dirRed = dirRed;
  }

  public InetAddress getMascaraRed()
  {
    return mascaraRed;
  }

  public void setMascaraRed( InetAddress mascaraRed )
  {
    this.mascaraRed = mascaraRed;
  }

  public InetAddress getDirRouter()
  {
    return dirRouter;
  }

  public void setDirRouter( InetAddress dirRouter )
  {
    this.dirRouter = dirRouter;
  }

  public int getTimpoLimite()
  {
    return timpoLimite;
  }

  public void setTimpoLimite( int timpoLimite )
  {
    this.timpoLimite = timpoLimite;
  }

  public Map<InetAddress, Asignacion> getIpsAsignadas()
  {
    return ipsAsignadas;
  }

  public void setIpsAsignadas( Map<InetAddress, Asignacion> ipsAsignadas )
  {
    this.ipsAsignadas = ipsAsignadas;
  }

  public InetAddress generarIp( InetAddress ipDeAsigancion ) throws UnknownHostException
  {
    byte[] direccRed = this.dirRed.getAddress();
    byte[] mascaraBytes = this.mascaraRed.getAddress();
    boolean encontro = false;
    int[] mascara = new int[4];
    byte[] ipDest = new byte[4];

    mascara[0] = mascaraBytes[0] & 0xff;
    mascara[1] = mascaraBytes[1] & 0xff;
    mascara[2] = mascaraBytes[2] & 0xff;
    mascara[3] = mascaraBytes[3] & 0xff;

    ipDest[0] = direccRed[0];
    for ( int i = mascara[0]; i <= BROADCAST && !encontro; i++, ipDest[0]++ )
    {
      ipDest[1] = direccRed[1];
      for ( int j = mascara[1]; j <= BROADCAST && !encontro; j++, ipDest[1]++ )
      {
        ipDest[2] = direccRed[2];
        for ( int k = mascara[2]; k <= BROADCAST && !encontro; k++, ipDest[2]++ )
        {
          int l = direccRed[3] + 1;
          ipDest[3] = ( byte ) ( direccRed[3] + 1 );
          for ( ; l < BROADCAST && !encontro; l++, ipDest[3]++ )
          {
            ipDeAsigancion = InetAddress.getByAddress( ipDest );
            if ( !this.estaEnOferta( ipDeAsigancion ) && !this.estaAsignada( ipDeAsigancion ) )
            {
              encontro = true;
              return ipDeAsigancion;
            }
          }
        }
      }
    }
    return InetAddress.getByAddress( ipDest );
  }

  boolean estaAsignada( InetAddress ipDeAsigancion )
  {
    if ( this.ipsAsignadas.containsKey( ipDeAsigancion ) )
    {
      Asignacion asig = this.ipsAsignadas.get( ipDeAsigancion );
      return asig.getEstado().compareTo( Asignacion.ASIGNADA ) == 0;
    }

    return false;
  }

  boolean estaEnOferta( InetAddress ipDeAsigancion )
  {
    if ( this.ipsAsignadas.containsKey( ipDeAsigancion ) )
    {
      Asignacion asig = this.ipsAsignadas.get( ipDeAsigancion );
      return asig.getEstado().compareTo( Asignacion.EN_OFERTA ) == 0;
    }

    return false;
  }

  @Override
  public String toString()
  {
    return "Red{" + "dirRouter=" + dirRouter + ", dirRed=" + dirRed + ", mascaraRed=" + mascaraRed + ", timpoLimite=" + timpoLimite + ", ipsAsignadas=" + ipsAsignadas + '}';
  }


}
