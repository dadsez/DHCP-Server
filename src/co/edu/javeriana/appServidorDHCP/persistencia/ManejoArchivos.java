/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.javeriana.appServidorDHCP.persistencia;


import co.edu.javeriana.appServidorDHCP.negocio.Sistema;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;


/**
 *
 * @author DAVID FELIPE
 */
public class ManejoArchivos
{

  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Lee configuraciÃ³n inicial del servidor de un archivo texto.
   * @return
   *
   * @throws java.lang.Exception
   */
  public static boolean leerConfServidor( Sistema sistema ) throws Exception
  {
    File archivo = null;
    FileReader fr = null;
    BufferedReader br = null;
    InetAddress ipServidor, dirRouter, dirRed, mascRed;
    int tmpLimite;

    try
    {
      archivo = new File( "./Conf.txt" );
      fr = new FileReader( archivo );
      br = new BufferedReader( fr );

      String linea = br.readLine();  //Lee ip del servidor.
      ipServidor = InetAddress.getByName( linea );
      sistema.setIpServidor( ipServidor );

      linea = br.readLine(); //Lee cantidad de subredes a manejar.
      int cantRedes = Integer.parseInt( linea );
      for ( int i = 0; i < cantRedes; i++ )
      {
        linea = br.readLine(); //Lee gateway subred No. i
        dirRouter = InetAddress.getByName( linea );

        linea = br.readLine(); //Lee direccion de red subred No. i
        dirRed = InetAddress.getByName( linea );

        linea = br.readLine(); //Lee mÃ¡scara de Red subred No. i
        mascRed = InetAddress.getByName( linea );

        linea = br.readLine(); //Lee tiempo de asignacion subred No. i
        tmpLimite = Integer.parseInt( linea );

        sistema.agregarRed( dirRouter, dirRed, mascRed, tmpLimite );
      }
      return true;
    }
    catch ( IOException | NumberFormatException e )
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        if ( null != fr )
        {
          fr.close();
        }
      }
      catch ( Exception e2 )
      {
        e2.printStackTrace();
      }
    }
    return false;
  }

  public static void generarLog( Sistema sistema )
  {
    throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
  }

}
