package com.sameer.scheduler.job;


import sun.misc.Unsafe;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;

import static java.util.Collections.singletonList;
import static javax.tools.JavaFileObject.Kind.SOURCE;

public class JobGeneric
{
    Class myClass=null;
   public static ClassLoader myClassLoader=null;

    public JobGeneric(String className) throws IllegalAccessException, InstantiationException {
        final String path = "com.sameer.scheduler.job";
        final String fullClassName = path.replace('.', '/') + "/" + className;
        final StringBuilder source = new StringBuilder();
        source.append("package " + path+";");
        source.append("import com.sameer.scheduler.client.CovidClient;");
        source.append("import com.sameer.scheduler.client.CovidClient;\n" +
                "import com.sameer.scheduler.model.TimerInfo;\n" +
                "import lombok.SneakyThrows;\n" +
                "import lombok.extern.slf4j.Slf4j;\n" +
                "import org.quartz.Job;\n" +
                "import org.quartz.JobDataMap;\n" +
                "import org.quartz.JobExecutionContext;\n" +
                "import org.quartz.JobExecutionException;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Component;" +
                "import java.time.LocalDateTime;\n" +
                "@Component\n" +
                "@Slf4j\n");
        source.append("public class " + className + " implements Job {\n");
        source.append(" @Autowired\n" +
                "    CovidClient covidClient;\n");
        source.append(" @SneakyThrows\n" +
                "    @Override\n" +
                "    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {\n" +
                "        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();\n" +
                "        TimerInfo timerInfo = (TimerInfo) jobDataMap.get("+className+".class.getSimpleName());\n" +
                "        log.info(\""+className+": \" + timerInfo.getCallbackData() + \" executed at \" + LocalDateTime.now());\n" +
                "\n" +
                "        //  log.info(\"Remaining time is : \" + timerInfo.getRemainingFireCount());\n" +
                "\n" +
                "        covidClient.getAvailableVaccine(timerInfo.getVaccineRequest());\n" +
                "    }");
        source.append("}\n");
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        final SimpleJavaFileObject simpleJavaFileObject
                = new SimpleJavaFileObject(URI.create(fullClassName + ".java"), SOURCE) {

            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }

            @Override
            public OutputStream openOutputStream() throws IOException {
                return byteArrayOutputStream;
            }
        };

        final JavaFileManager javaFileManager = new ForwardingJavaFileManager(
                ToolProvider.getSystemJavaCompiler().
                        getStandardFileManager(null, null, null)) {

            @Override
            public JavaFileObject getJavaFileForOutput(
                    Location location,String className,
                    JavaFileObject.Kind kind,
                    FileObject sibling) {
                return simpleJavaFileObject;
            }
        };
        ToolProvider.getSystemJavaCompiler().getTask(null, javaFileManager, null, null, null, singletonList(simpleJavaFileObject)).call();
        final byte[] bytes = byteArrayOutputStream.toByteArray();

        // use the unsafe class to load in the class bytes
        Field f = null;

        {
            try {
                f = Unsafe.class.getDeclaredField("theUnsafe");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        f.setAccessible(true);
        final Unsafe unsafe = (Unsafe) f.get(null);
        ClassLoader classLoader= ClassLoader.getSystemClassLoader();
        final Class aClass = unsafe.defineClass(fullClassName, bytes, 0, bytes.length,classLoader,null);
        myClass=aClass;
        myClassLoader=classLoader;


        final Object o = aClass.newInstance();
        System.out.println(o);

    }

    public Class<? extends JobGeneric> getClassName() {
        return myClass;
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
     JobGeneric JobGeneric =new JobGeneric("Job1");
    }
}
