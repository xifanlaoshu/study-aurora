/*
 * Created on 2007-10-30
 */
package aurora.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import uncertain.datatype.DataType;
import uncertain.datatype.DataTypeRegistry;
import aurora.service.validation.Parameter;

public class BindParameter {
    
    // How to access parameter value for input
    String      input_path;
    String      output_path;
    //    int         jdbc_data_type = Types.OTHER;
    int         position  = 0;
    boolean     is_sql_statement = false;
    boolean     is_input         = true;
    boolean     is_output        = false;
    DataType    data_type;
    String      database_type_name;
    
    boolean     isAutoGeneratedKey = false;
    
    /**
     * @param Name name of parameter
     * @param access_path 
     */
    public BindParameter( String access_path, int data_type ) {
        //this.name = name;
        this.input_path = access_path;
        //this.jdbc_data_type = data_type;
    }
    
    public BindParameter( String access_path ){
        this(access_path, Types.OTHER);
    }
    
    public void copyFrom( Parameter p ){        
        input_path = p.getInputPath();
        output_path = p.getOutputPath();
        is_input = p.getInput();
        is_output = p.getOutput();
        data_type = DataTypeRegistry.getInstance().getDataType(p.getDataType());
        database_type_name = p.getDatabaseTypeName();
        isAutoGeneratedKey = p.isAutoGeneratedKey();
    }
    
    public BindParameter(){
        
    }

    /**
     * @return the data_type
     */
    public DataType getDataType() {
        return data_type;
    }

    /**
     * @param data_type the data_type to set
     */
    public void setDataType(DataType data_type) {
        this.data_type = data_type;
    }
    
    public String getDataTypeName(){
        return data_type==null?null:data_type.getClass().getName();
    }
    
    public void setDataTypeName(String name){
        data_type = DataTypeRegistry.getInstance().getType(name);
        if(data_type==null) throw new IllegalArgumentException("Unknown data type: "+name);
    }

    /**
     * @return the jdbc_data_type
     */
    
    public int getJdbcDataType() {
        return data_type==null?Types.OTHER:data_type.getSqlType();
    }

    /**
     * @param jdbc_data_type the jdbc_data_type to set
     */
    public void setJdbcDataType(int jdbc_data_type) {
        data_type = DataTypeRegistry.getInstance().getType(jdbc_data_type);
        if(data_type==null) throw new IllegalArgumentException("Unknown JDBC type: "+jdbc_data_type);
    }
    
    public void setStatement( int index, PreparedStatement stmt, Object value )
        throws SQLException
    {
        DataType dt = data_type;
        if(dt==null){
            if(value==null || value.equals(null)){
                stmt.setNull(index, Types.VARCHAR);
                return;
            }else{
                dt = DataTypeRegistry.getInstance().getDataType(value.getClass());
                if(dt==null) throw new IllegalArgumentException("Can't get data type for value ["+value+"] class: "+value.getClass().getName());
            }
        }
        dt.setParameter(stmt, index, value);
    }
    
    public boolean isInput(){
        return is_input;
    }
    
    public boolean isSQLStatement(){
        return is_sql_statement;
    }
    
    public String getPath(){
        return input_path;
    }
    
    public void setPath(String path){
        this.input_path = path;
    }
    
    public String toString(){
        StringBuffer buf = new StringBuffer();
        buf.append("<parameter inputPath=\"").append(input_path)
           .append("\" output_path=\"").append(output_path).append("\" DataType=\"")
           .append(this.data_type).append("\" />");
        return buf.toString();
    }

}
