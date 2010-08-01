package com.senacor.ddt.objectmatrix.writeable;

import com.senacor.ddt.objectmatrix.DefaultStringMatrix;
import com.senacor.ddt.objectmatrix.MatrixReadFailedException;

public class DefaultWriteableStringMatrix extends DefaultStringMatrix implements WriteableStringMatrix {
  public interface StringMatrixWriter {
    // not yet implemented
  }
  
  public DefaultWriteableStringMatrix(final StringMatrixReader reader, final StringMatrixWriter writer) {
    super(reader);
  }
  
  public DefaultWriteableStringMatrix(final StringMatrixWriter writer) {
    this(new StringMatrixReader() {
      
      public String getString(final int colIndex, final int rowIndex) throws IndexOutOfBoundsException,
          MatrixReadFailedException {
        throw new IndexOutOfBoundsException();
      }
      
      public int getNumberOfRows() throws MatrixReadFailedException {
        return 0;
      }
      
      public int getNumberOfColumns() throws MatrixReadFailedException {
        return 0;
      }
      
      public String getIdentifier() throws MatrixReadFailedException {
        return "";
      }
      
    }, writer);
  }
}
