package co.edu.javeriana.appServidorDHCP.negocio;


import java.util.Enumeration;
import java.util.Hashtable;


/**
 *
 * @author David Suárez, Orlando Abaunza.
 */
public class DHCPOptions
{

  public static final int OPTION_END = 255;

  public static final int OPTION_NETMASK = 1;
  public static final int OPTION_DHCP_IP_ADRESS_REQUESTED = 50;
  public static final int OPTION_DHCP_IP_LEASE_TIME = 51;
  public static final int OPTION_DHCP_OVERLOAD = 52;
  public static final int OPTION_DHCP_MESSAGE_TYPE = 53;
  public static final int OPTION_DHCP_SERVER_IDENTIFIER = 54;
  public static final int OPTION_DHCP_PARAMETER_REQUEST_LIST = 55;
  public static final int OPTION_DHCP_RENEWAL_TIME = 58;
  public static final int OPTION_DHCP_REBIND_TIME = 59;


  /**
   * This inner class represent an entry in the Option Table
   */
  class DHCPOptionsEntry
  {

    protected byte code;
    protected byte length;
    protected byte content[];

    public DHCPOptionsEntry( byte entryCode, byte entryLength,
                             byte entryContent[] )
    {
      code = entryCode;
      length = entryLength;
      content = entryContent;
    }

    public String toString()
    {
      return "Code: " + code + "\nContent: " + new String( content );
    }

  }

  private Hashtable<Byte, DHCPOptionsEntry> optionsTable = null;
  public Hashtable<Byte, DHCPOptionsEntry> getOptionsTable()
  {
    return optionsTable;
  }

  public void setOptionsTable( Hashtable<Byte, DHCPOptionsEntry> optionsTable )
  {
    this.optionsTable = optionsTable;
  }

  public DHCPOptions()
  {
    optionsTable = new Hashtable<Byte, DHCPOptionsEntry>();
  }

  /**
   * Removes option with specified bytecode
   * @param entryCode The code of option to be removed
   */

  public void removeOption( byte entryCode )
  {
    optionsTable.remove( new Byte( entryCode ) );
  }

  /**
   * Returns true if option code is set in list; false otherwise
   * @param entryCode The node's option code
   * @return true if option is set, otherwise false
   */
  public boolean contains( byte entryCode )
  {
    return optionsTable.containsKey( new Byte( entryCode ) );
  }

  /**
   * Determines if list is empty
   * @return true if there are no options set, otherwise false
   */
  public boolean isEmpty()
  {
    return optionsTable.isEmpty();
  }

  /**
   * Fetches value of option by its option code
   * @param entryCode The node's option code
   * @return byte array containing the value of option entryCode. null is
   * returned if option is not set.
   */
  public byte[] getOption( byte entryCode )
  {
    if ( this.contains( entryCode ) )
    {
      DHCPOptionsEntry ent = optionsTable.get( new Byte( entryCode ) );
      return ent.content;
    }
    else
    {
      return null;
    }
  }

  /**
   * Changes an existing option to new value
   * @param entryCode The node's option code
   * @param value Content of node option
   */
  public void setOption( byte entryCode, byte value[] )
  {
    DHCPOptionsEntry opt = new DHCPOptionsEntry( entryCode, ( byte ) value.length, value );
    optionsTable.put( new Byte( entryCode ), opt );
  }

  /**
   * Returns the option value of a specified option code in a byte array
   * @param length Length of option content
   * @param position Location in array of option node
   * @param options The byte array of options
   * @return byte array containing the value for the option
   */
  private byte[] getArrayOption( int length, int position, byte options[] )
  {
    byte value[] = new byte[( int ) length];
    for ( int i = 0; i < ( int ) length; i++ )
    {
      value[i] = options[position + i];
    }
    return value;
  }

  /**
   * Converts an options byte array to a linked list
   * @param optionsArray The byte array representation of the options list
   */
  public void internalize( byte[] optionsArray )
  {

    /* Assume options valid and correct */
    int pos = 4; // ignore vendor magic cookie
    byte code, length;
    byte value[];

    while ( optionsArray[pos] != ( byte ) 255 )
    { // until end option
      code = optionsArray[pos++];
      length = optionsArray[pos++];
      value = getArrayOption( length, pos, optionsArray );
      setOption( code, value );
      pos += length; // increment position pointer
    }
  }

  /**
   * Converts a linked options list to a byte array
   * @return array representation of optionsTable
   */
  // todo provide overflow return
  public byte[] externalize()
  {
    byte[] options = new byte[312];

    options[0] = ( byte ) 99;
    options[1] = ( byte ) 130;
    options[2] = ( byte ) 83;
    options[3] = ( byte ) 99;

    int position = 4;
    Enumeration<DHCPOptionsEntry> e = optionsTable.elements();

    while ( e.hasMoreElements() )
    {
      DHCPOptionsEntry entry = e.nextElement();
      options[position++] = entry.code;
      options[position++] = entry.length;
      for ( int i = 0; i < entry.length; ++i )
      {
        options[position++] = entry.content[i];
      }
    }

    options[position] = ( byte ) 255;
    return options;
  }

  /**
   * Prints the options linked list: For testing only.
   */
  public void printList()
  {
    System.out.println( optionsTable.toString() );
  }

}
