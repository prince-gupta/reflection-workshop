import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PrivateObject {

    private String privateString = null;

    public PrivateObject(){}
//    public PrivateObject(String privateString) {
//        this.privateString = privateString;
//    }

    private String getPrivateString(){
        return this.privateString;
    }

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //PrivateObject privateObject = new PrivateObject("The Private Value");

        Field privateStringField = PrivateObject.class.
                getDeclaredField("privateString");

        privateStringField.setAccessible(true);

        PrivateObject ob1 = new PrivateObject();
        privateStringField.set(ob1, "hello");

        String fieldValue = (String) privateStringField.get(ob1);
        System.out.println("fieldValue = " + fieldValue);


        Method privateStringMethod = PrivateObject.class.
                getDeclaredMethod("getPrivateString", null);

        privateStringMethod.setAccessible(true);

//        String returnValue = (String)
//                privateStringMethod.invoke(privateObject, null);

        //System.out.println("returnValue = " + returnValue);
    }
}
