package org.broadinstitute.sting.utils.genotype.glf;

import net.sf.samtools.util.BinaryCodec;

/*
 * Copyright (c) 2009 The Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * @author aaron
 *         <p/>
 *         Class VariableLengthCall
 *         <p/>
 *         This class represents variable length genotype calls in the GLF format.
 *         Currently a lot of parameters need to be provided, but we may be able to thin
 *         those down as we understand what we have to specify and what we can infer.
 */
public class VariableLengthCall extends GLFRecord {
    // our fields, corresponding to the glf spec
    private short lkHom1 = 0;
    private short lkHom2 = 0;
    private short lkHet = 0;
    private int indelLen1 = 0;
    private int indelLen2 = 0;
    private final short indelSeq1[];
    private final short indelSeq2[];

    // our size, which is immutable, in bytes
    private final int size;


    /**
     * the default constructor
     *
     * @param refBase    the reference base
     * @param offset     the location, as an offset from the previous glf record
     * @param readDepth  the read depth at the specified postion
     * @param rmsMapQ    the root mean square of the mapping quality
     * @param lkHom1     the negitive log likelihood of the first homozygous indel allele, from 0 to 255
     * @param lkHom2     the negitive log likelihood of the second homozygous indel allele, from 0 to 255
     * @param lkHet      the negitive log likelihood of the heterozygote,  from 0 to 255
     * @param indelSeq1  the sequence for the first indel allele
     * @param indelSeq2  the sequence for the second indel allele
     */
    public VariableLengthCall(char refBase,
                       long offset,
                       int readDepth,
                       short rmsMapQ,
                       double lkHom1,
                       double lkHom2,
                       double lkHet,
                       int indelOneLength,
                       final short indelSeq1[],
                       int indelTwoLength,
                       final short indelSeq2[]) {
        super(refBase, offset, GLFRecord.toCappedShort(findMin(new double[]{lkHom1, lkHom2, lkHet})), readDepth, rmsMapQ);
        this.lkHom1 = GLFRecord.toCappedShort(lkHom1);
        this.lkHom2 = GLFRecord.toCappedShort(lkHom2);
        this.lkHet = GLFRecord.toCappedShort(lkHet);
        this.indelLen1 = indelOneLength;
        this.indelLen2 = indelTwoLength;
        this.indelSeq1 = indelSeq1;
        this.indelSeq2 = indelSeq2;
        size = 16 + indelSeq1.length + indelSeq2.length;

    }

    /**
     * Write out the record to a binary codec
     *
     * @param out the binary codec to write to
     */
    void write(BinaryCodec out) {
        super.write(out);
        out.writeByte(lkHom1);
        out.writeByte(lkHom2);
        out.writeByte(lkHet);
        out.writeShort(new Integer(indelLen1).shortValue());
        out.writeShort(new Integer(indelLen2).shortValue());
        for (short anIndelSeq1 : indelSeq1) {
            out.writeUByte(anIndelSeq1);
        }
        for (short anIndelSeq2 : indelSeq2) {
            out.writeUByte(anIndelSeq2);
        }
    }

    /** @return RECORD_TYPE.VARIABLE */
    public RECORD_TYPE getRecordType() {
        return RECORD_TYPE.VARIABLE;
    }

    /** @return the size of the record, which is the size of our fields plus the generic records fields */
    public int getByteSize() {
        return size + super.getByteSize();
    }

    public short getLkHom1() {
        return lkHom1;
    }

    public short getLkHom2() {
        return lkHom2;
    }

    public short getLkHet() {
        return lkHet;
    }

    public short[] getIndelSeq1() {
        return indelSeq1;
    }

    public short[] getIndelSeq2() {
        return indelSeq2;
    }

    public int getIndelLen2() {
        return indelLen2;
    }

    public int getIndelLen1() {
        return indelLen1;
    }
}
