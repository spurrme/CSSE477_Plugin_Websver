import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class PluginClassLoader extends ClassLoader {
    File directory;
    public PluginClassLoader (File dir) {
		directory = dir;
	}
    public Class<?> loadClass (String name) throws ClassNotFoundException { 
        return loadClass(name, true); 
      }
    public Class<?> loadClass (String classname, boolean resolve) throws ClassNotFoundException {
        try {
          Class<?> c = findLoadedClass(classname);
          if (c == null) {
            try { c = findSystemClass(classname); }
            catch (Exception ex) {}
          }
          if (c == null) {
            String filename = classname.replace('.',File.separatorChar)+".class";
            File f = new File(directory, filename);
            int length = (int) f.length();
            byte[] classbytes = new byte[length];
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            in.readFully(classbytes);
            in.close();
            c = defineClass(classname, classbytes, 0, length);
          }
          if (resolve) resolveClass(c);
          return c;
        }
        catch (Exception ex) { throw new ClassNotFoundException(ex.toString()); }
      }
}
