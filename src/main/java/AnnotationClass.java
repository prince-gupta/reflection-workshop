import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@MyAnnotation(name="someName",  value = "Hello World")
public class AnnotationClass {

    @MyAnnotation(name="methodName",  value = "Hello World Method")
    public void doSomething(){
        System.out.println("Execute Method");
    }

    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException {
        Class annotationClass = AnnotationClass.class;

//        Annotation[] annotations = annotationClass.getAnnotations();
//
//        for(Annotation annotation : annotations){
//            if(annotation instanceof MyAnnotation){
//                MyAnnotation myAnnotation = (MyAnnotation) annotation;
//                System.out.println("name: " + myAnnotation.name());
//                System.out.println("value: " + myAnnotation.value());
//            }
//        }
//
//
//
//        Annotation annotation = annotationClass.getAnnotation(MyAnnotation.class);
//
//        if(annotation instanceof MyAnnotation){
//            MyAnnotation myAnnotation = (MyAnnotation) annotation;
//            System.out.println("name: " + myAnnotation.name());
//            System.out.println("value: " + myAnnotation.value());
//        }

        Method method = annotationClass.getMethod("doSomething");
        Annotation[] methodAnnotations = method.getDeclaredAnnotations();

        for(Annotation methodAnnotation : methodAnnotations){
            if(methodAnnotation instanceof MyAnnotation){
                MyAnnotation myAnnotation = (MyAnnotation) methodAnnotation;
                System.out.println("name: " + myAnnotation.name());
                System.out.println("value: " + myAnnotation.value());
            }
        }
    }
}
