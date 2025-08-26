// Decompiled with: CFR 0.152
// Class Version: 17
package jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ResultSetImpl implements NDVResultSet {
    private Map<String, Object>[] data;
    private Object[][] values;
    private int indexData = -1;

    public ResultSetImpl(ResultSet resultSet) throws Exception {
        try {
            resultSet.last();
            int rowCount = resultSet.getRow();
            resultSet.beforeFirst();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            this.data = new HashMap[rowCount];
            for (int i = 0; i < this.data.length; i++) {
                this.data[i] = new HashMap<>();
            }
            this.values = new Object[rowCount][columnCount];
            int row = 0;
            while (resultSet.next()) {
                for (int col = 1; col <= columnCount; col++) {
                    String tableName = resultSetMetaData.getTableName(col);
                    String columnName = resultSetMetaData.getColumnName(col);
                    Object value = resultSet.getObject(col);
                    this.data[row].put(columnName.toLowerCase(), value);
                    this.data[row].put((tableName.toLowerCase() + "." + columnName.toLowerCase()), value);
                    this.values[row][col - 1] = value;
                }
                row++;
            }
        } catch (Exception exception) {
            throw exception;
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.getStatement().close();
                    resultSet.close();
                } catch (Exception ignored) {}
            }
        }
    }

    @Override
    public void dispose() {
        if (this.data != null) {
            for (Map<String, Object> map : this.data) {
                if (map != null) {
                    map.clear();
                }
            }
            this.data = null;
        }
        if (this.values != null) {
            for (Object[] row : this.values) {
                for (int j = 0; j < row.length; j++) {
                    row[j] = null;
                }
            }
            this.values = null;
        }
    }

    @Override
    public boolean next() throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        ++this.indexData;
        return this.indexData < this.data.length;
    }

    @Override
    public boolean first() throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        this.indexData = 0;
        return true;
    }

    @Override
    public boolean gotoResult(int n) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (n < 0 || n >= this.data.length) {
            throw new Exception("Index out of bound");
        }
        this.indexData = n;
        return true;
    }

    @Override
    public boolean gotoFirst() throws Exception {
        if (this.data == null || this.data.length == 0) {
            throw new Exception("No data available");
        }
        this.indexData = 0;
        return true;
    }

    @Override
    public void gotoBeforeFirst() {
        this.indexData = -1;
    }

    @Override
    public boolean gotoLast() throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        this.indexData = this.data.length - 1;
        return true;
    }

    @Override
    public int getRows() throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        return this.data.length;
    }

    @Override
    public byte getByte(int n) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return (byte) ((Integer) this.values[this.indexData][n - 1]).intValue();
    }

    @Override
    public byte getByte(String string) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return (byte) ((Integer) this.data[this.indexData].get(string.toLowerCase())).intValue();
    }

    @Override
    public int getInt(int n) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return ((Number) this.values[this.indexData][n - 1]).intValue();
    }

    @Override
    public int getInt(String string) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return (Integer) this.data[this.indexData].get(string.toLowerCase());
    }

    @Override
    public float getFloat(int n) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return ((Float) this.values[this.indexData][n - 1]).floatValue();
    }

    @Override
    public float getFloat(String string) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return ((Float) this.data[this.indexData].get(string.toLowerCase())).floatValue();
    }

    @Override
    public double getDouble(int n) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return (Double) this.values[this.indexData][n - 1];
    }

    @Override
    public double getDouble(String string) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return (Double) this.data[this.indexData].get(string.toLowerCase());
    }

    @Override
    public long getLong(int n) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return (Long) this.values[this.indexData][n - 1];
    }

    @Override
    public long getLong(String string) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return (Long) this.data[this.indexData].get(string.toLowerCase());
    }

    @Override
    public String getString(int n) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return String.valueOf(this.values[this.indexData][n - 1]);
    }

    @Override
    public String getString(String string) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return String.valueOf(this.data[this.indexData].get(string.toLowerCase()));
    }

    @Override
    public Object getObject(int n) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return this.values[this.indexData][n - 1];
    }

    @Override
    public Object getObject(String string) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return this.data[this.indexData].get(string.toLowerCase());
    }

    @Override
    public boolean getBoolean(int n) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        try {
            return (Integer) this.values[this.indexData][n - 1] == 1;
        } catch (Exception exception) {
            return (Boolean) this.values[this.indexData][n - 1];
        }
    }

    @Override
    public boolean getBoolean(String string) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        try {
            return (Integer) this.data[this.indexData].get(string.toLowerCase()) == 1;
        } catch (Exception exception) {
            return (Boolean) this.data[this.indexData].get(string.toLowerCase());
        }
    }

    @Override
    public Timestamp getTimestamp(int n) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return (Timestamp) this.values[this.indexData][n - 1];
    }

    @Override
    public Timestamp getTimestamp(String string) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return (Timestamp) this.data[this.indexData].get(string.toLowerCase());
    }

    @Override
    public short getShort(int n) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return (short) ((Integer) this.values[this.indexData][n - 1]).intValue();
    }

    @Override
    public short getShort(String string) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return (short) ((Integer) this.data[this.indexData].get(string.toLowerCase())).intValue();
    }
}
