package jdbc;

import java.sql.Timestamp;

public interface NDVResultSet {
    public byte getByte(int var1) throws Exception;

    public byte getByte(String var1) throws Exception;

    public int getInt(int var1) throws Exception;

    public int getInt(String var1) throws Exception;

    public short getShort(int var1) throws Exception;

    public short getShort(String var1) throws Exception;

    public float getFloat(int var1) throws Exception;

    public float getFloat(String var1) throws Exception;

    public double getDouble(int var1) throws Exception;

    public double getDouble(String var1) throws Exception;

    public long getLong(int var1) throws Exception;

    public long getLong(String var1) throws Exception;

    public String getString(int var1) throws Exception;

    public String getString(String var1) throws Exception;

    public boolean getBoolean(int var1) throws Exception;

    public boolean getBoolean(String var1) throws Exception;

    public Object getObject(int var1) throws Exception;

    public Object getObject(String var1) throws Exception;

    public Timestamp getTimestamp(int var1) throws Exception;

    public Timestamp getTimestamp(String var1) throws Exception;

    public void dispose();

    public boolean next() throws Exception;

    public boolean first() throws Exception;

    public boolean gotoResult(int var1) throws Exception;

    public boolean gotoFirst() throws Exception;

    public void gotoBeforeFirst();

    public boolean gotoLast() throws Exception;

    public int getRows() throws Exception;
}
