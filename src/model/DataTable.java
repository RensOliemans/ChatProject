package model;

import java.util.*;

/**
 * Created by coen on 5-4-2016.
 */
public class DataTable {

    private List<List<Integer>> data = new ArrayList<List<Integer>>();
    private int columns;

    // get number of columns in table
    public int getNColumns(){
        return this.columns;
    }

    // get number of rows in table
    public int getNRows(){
        return this.data.size();
    }

    // construct a data table
    public DataTable(int nColumns){
        if (nColumns < 0){
            throw new IllegalArgumentException("nColumns must be >= 0");
        }
        this.columns = nColumns;
    }

    // set a table cell to a value
    public void set(int row, int column, int value) {
        if (column < 0 || column >= this.columns){
            throw new IllegalArgumentException("Column index (" + column + ") out of range (0.." + this.columns + ").");
        }

        if (row < 0){
            throw new IllegalArgumentException("Row index < 0");
        }

        while (this.data.size() <= row) {
            List<Integer> newRow = new ArrayList<Integer>();
            for (int i = 0; i < this.columns; i++) {
                newRow.add(0);
            }
            this.data.add(newRow);
        }

        this.data.get(row).set(column, value);
    }

    // get a value from the table
    public int get(int row, int column){
        if (column < 0 || column >= this.columns){
            throw new IllegalArgumentException("Column index (" + column + ") out of range (0.." + this.columns + ").");
        }

        if (row < 0 || row >= this.data.size()){
            throw new IllegalArgumentException("Row index (" + row + ") out of range (0.." + this.data.size() + ").");
        }

        return this.data.get(row).get(column);
    }

    // insert a row into the table
    public void setRow(int row, Integer[] values){
        if (values.length != this.columns){
            throw new IllegalArgumentException("values array size (" + values.length + ") must match DataTable nColumns (" + this.columns + ").");
        }

        for (int i=0; i<values.length; i++){
            this.set(row, i, values[i]);
        }
    }

    // retreive a row from the data table
    public Integer[] getRow(int row){
        if (row < 0 || row >= this.data.size()){
            throw new IllegalArgumentException("Row index (" + row + ") out of range (0.." + this.data.size() + ").");
        }

        Integer[] returnArray = new Integer[this.columns];
        for(int i=0; i<this.columns; i++){
            returnArray[i] = this.get(row, i);
        }
        return returnArray;
    }

    // add a row to the end of the data table
    public void addRow(Integer[] values){
        if (values.length != this.columns){
            throw new IllegalArgumentException("values array size (" + values.length + ") must match DataTable nColumns (" + this.columns + ").");
        }

        this.setRow(this.data.size(), values);
    }

}
