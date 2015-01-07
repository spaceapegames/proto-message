package com.spaceape.protobuf;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class IntFieldDescriptorTest {
    @Test
    public void testGetFieldValue() throws Exception{
        byte [] data = new TestBean2().toByteArray();
        TestBean2 newBean2 = new TestBean2();
        newBean2.mergeFrom(CodedInputStream.newInstance(data), new CodeGenContext());
        Assert.assertEquals(newBean2.getCounter(), 3);
        Assert.assertEquals(newBean2.getCounter2(), 6);

        TestBean newBean = new TestBean();
        newBean.mergeFrom(CodedInputStream.newInstance(data), new CodeGenContext());
        Assert.assertEquals(newBean.getCounter(), 3);
    }
}

class TestBean extends GeneratedMessage{
    private int counter = 3;

    public int getCounter () {
        return counter;
    }

    public void setCounter(int counter){
        this.counter = counter;
    }

    protected boolean readField(int fieldNumber, CodedInputStream input, CodeGenContext context) throws IOException{
        if (super.readField(fieldNumber, input, context)){
            return true;
        }
        if (fieldNumber == 1){
            setCounter(input.readInt32());
            return true;
        }
        else {
            return false;
        }
    }
    protected void writeField(CodedOutputStream output, CodeGenContext context) {
        try {
            output.writeInt32(1, getCounter());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

class TestBean2 extends TestBean{

    private int counter2 = 6;
    public int getCounter2 () {
        return counter2;
    }

    public void setCounter2(int counter2){
        this.counter2 = counter2;
    }

    protected boolean readField(int fieldNumber, CodedInputStream input, CodeGenContext context) throws IOException{
        if (super.readField(fieldNumber, input, context)){
            return true;
        }
        if (fieldNumber == 2){
            counter2 = input.readInt32();
            return true;
        }
        else {
            return false;
        }
    }
    protected void writeField(CodedOutputStream output, CodeGenContext context) {
        super.writeField(output, context);
        try {
            output.writeInt32(2, counter2);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}