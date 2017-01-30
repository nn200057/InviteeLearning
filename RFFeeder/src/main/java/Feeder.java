import java.io.*;
import java.util.*;

/**
 * Created by nathael on 30/01/17.
 */
public class Feeder {

    public static void main(String[] args) throws InterruptedException {
        Feeder f = new Feeder();

        List<String> filenames = new ArrayList<>();
        File[] files = new File("../logs tronqués/").listFiles();
        for(File file : files)
            try {
                filenames.add(file.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        f.feed("theResult.csv", filenames);
    }



    public void feed(String fileWhereToWrite, List<String> filenames) throws InterruptedException {
        List<String> alldata = new ArrayList<>();

        File fDest = new File(fileWhereToWrite);
        try {
            if(fDest.exists())
                fDest.delete();
            if (!fDest.createNewFile()) {
                System.err.println(fDest.getAbsolutePath() + ": cannot create file.");
                return;
            } else {
                System.out.println("Writing result in "+fDest.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println(fDest.getAbsolutePath() + ": cannot create file.");
            return;
        }

        for(String fileName : filenames) {
            int grade = -1;

            if(fileName.matches(".*[^0-3]")) {
                grade = fileName.charAt(fileName.length()-1)-'0';
            }
            if(grade < 0 || grade > 3) {
                System.err.println("Cannot find cut grade in file name: "+fileName);
                Thread.sleep(100);
                System.out.print("Please enter a Grade [0-3]: ");
                Scanner sc = new Scanner(System.in);
                while(true) {
                    String line = sc.nextLine();
                    int val = Integer.parseInt(line);
                    if (val >= 0 && val <= 3) {
                        grade = val;
                        break;
                    }
                }
            }

            File f = new File(fileName);
            System.out.println("Trying with file " + f.getAbsolutePath());
            Thread.sleep(100);

            try {
                FileReader fr = new FileReader(f);
                String strLine = toStringLine(grade, scanData(new Scanner(fr)));
                alldata.add(strLine);

                fr.close();
            } catch (FileNotFoundException e) {
                System.err.println("Impossible to create FileReader for " + fileName);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        writeToFile(fDest, alldata);
    }

    private String toStringLine(int grade, List<Map<String, Integer>> data) {
        // "grade;rotX0;rotY0;rotZ0;accX0;accY0;accZ0;...;accZ499"
        StringBuilder builder = new StringBuilder();
        builder.append(grade);
        for(Map<String, Integer> line : data) {
            builder .append(";")
                    .append(line.get("rotX"))
                    .append(";")
                    .append(line.get("rotY"))
                    .append(";")
                    .append(line.get("rotZ"))
                    .append(";")
                    .append(line.get("accX"))
                    .append(";")
                    .append(line.get("accY"))
                    .append(";")
                    .append(line.get("accZ"));
        }

        return builder.toString();
    }

    private void writeToFile(File fDest, List<String> alldata) {
        try {
            FileWriter fw = new FileWriter(fDest);

            // "class;rotX0;rotY0;rotZ0;accX0;accY0;accZ0;...;accZ499"
            fw.write("class");
            for (int i = 0; i < 500; i++) {
                fw.write(";rotX"+i);
                fw.write(";rotY"+i);
                fw.write(";rotZ"+i);
                fw.write(";accX"+i);
                fw.write(";accY"+i);
                fw.write(";accZ"+i);
            }
            fw.write("\n");

            for(String line : alldata) {
                fw.write(line+"\n");
            }

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Map<String, Integer>> scanData(Scanner sc) {
        List<Map<String, Integer>> data = new ArrayList<>();

        // timer,roll,pitch,yaw,accel_x,accel_y,accel_z,posx,posy,posz
        while(sc.hasNextLine()) {
            String newline = sc.nextLine();
            String[] tab = newline.replaceAll(" ","").split(",");

            try {
                Map<String, Integer> line = new HashMap<>();
                line.put("timer", (int) (Float.valueOf(tab[0]) * 100000));
                line.put("rotX", (int) (Float.valueOf(tab[1]) * 100000));
                line.put("rotY", (int) (Float.valueOf(tab[2]) * 100000));
                line.put("rotZ", (int) (Float.valueOf(tab[3]) * 100000));
                line.put("accX", (int) (Float.valueOf(tab[4]) * 100000));
                line.put("accY", (int) (Float.valueOf(tab[5]) * 100000));
                line.put("accZ", (int) (Float.valueOf(tab[6]) * 100000));
                line.put("posX", (int) (Float.valueOf(tab[7]) * 100000));
                line.put("posY", (int) (Float.valueOf(tab[8]) * 100000));
                line.put("posZ", (int) (Float.valueOf(tab[9]) * 100000));
                data.add(line);
            } catch(NumberFormatException ignored){}
        }

        return data;
    }

}
