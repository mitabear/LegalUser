import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

/**
 * Created by Madhumita Ghude on 12/5/2016 for use by Johnson and Johnson employees/contractors only
 */
public class LegalUser {

    //remember to change this path to reflect the folder where all of your patient files are!
    private static File dir = new File("C:\\Users\\mghude1\\Desktop\\Legal\\September\\");
    private static Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
    private static String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static String GMS = GMSDate();


    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.print("Choose an option:\n" +
                "1. Rename files\n" +
                "2. Rename split files and label Original\n" +
                "3. Make a list of all the patients\n");
        File[] files;
        int option = sc.nextInt();
        switch(option){
            //Rename files
            case 1:
                System.out.print("Who sent the file? \n" +
                        "1. Xarelto MBox\n" +
                        "2. Risperdal MBox\n" +
                        "3. Levaquin\n" +
                        "4. Other\n" +
                        "5. Risperdal\n" +
                        "6. Invokana\n" +
                        "7. GoMarkers\n");
                int sender = sc.nextInt();
                String ICH;
                files = dir.listFiles((File dir, String name) -> name.endsWith(".pdf") || name.endsWith(".PDF"));
                switch(sender){
                    case 1:
                        Xarelto_MBox(files);
                        break;
                    case 2:
                        System.out.print("ICH date: ");
                        ICH = sc.next();
                        Risperdal_MBox(files, ICH);
                        break;
                    case 3:
                        System.out.println("Extract the folders from the zip file you downloaded and enter the name of the file: ");
                        String zip = sc.next();
                        File newdir = new File(dir.getAbsoluteFile()+"\\"+zip); //file in which all patient folders are contained
                        files = newdir.listFiles();
                        System.out.print("ICH date: ");
                        ICH = sc.next();
                        Levaquin(files, ICH);
                        break;
                    case 4:
                        System.out.print("ICH date: ");
                        ICH = sc.next();
                        RenameFiles(files, ICH);
                        break;
                    case 5:
                        Risperdal(files);
                        break;
                    case 6:
                        System.out.print("Folder name: ");
                        String fname = sc.next();
                        if(fname.equals("a")){
                            files = dir.listFiles(new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String name) {
                                    return name.endsWith(".pdf") || name.endsWith(".pdf");
                                }
                            });
                        }else {
                            dir = new File(dir.getAbsolutePath() + "\\" + fname);
                            files = dir.listFiles();
                        }
                        System.out.print("ICH date: ");
                        ICH = sc.next();
                        Invokana(files, ICH);
                        break;
                    case 7:
                        System.out.print("Folder name: ");
                        fname = sc.next();  //it says I don't need to instantiate variable here so ok
                        newdir = new File(dir.getAbsolutePath()+"\\"+fname);
                        files = newdir.listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                File x = new File(dir, name);
                                return x.isDirectory();
                            }
                        });
                        System.out.print("ICH date: ");
                        ICH = sc.next();
                        GoMarkers(files, ICH);
                        break;
                }
                break;
            //Rename split files and label Original
            case 2:
                files = dir.listFiles((dir, name) -> (name.endsWith(".pdf") || name.endsWith(".PDF")));
                RenameSplitFiles(files);
                break;
            //Count the patients and print out a list
            case 3:
                files = dir.listFiles((dir, name) -> (name.endsWith(".pdf") || name.endsWith(".PDF"))&&(name.contains("GMS")));
                CountPatients(files);
                break;
        }
    }

    private static void Xarelto_MBox(File[] files){
        int renamed = 0;
        for(File i: files) {
            String name = i.getName().substring(0, i.getName().indexOf(".pdf"));
            if (!name.startsWith("Xarelto")) {
                //File name contains patient name in format First_Last_YYYY.MM.DD
                int k = name.indexOf('_');
                String first = name.substring(0, k);
                String middle = name.substring(k + 1, name.indexOf('_', k + 1));
                String last;
                k = name.indexOf('_', k + 1);
                if (name.charAt(k + 1) == '2') {  //no middle name
                    last = middle;
                    middle = "";
                } else {  //is last name
                    last = name.substring(k + 1, name.indexOf('_', k + 1));
                }
                //ICH date
                int a = name.indexOf("2017.")+4;
                int year = new Integer(name.substring(name.indexOf("2"), a));
                int month = new Integer(name.substring(a + 1, name.indexOf(".", a + 1)));
                int date = new Integer(name.substring(name.indexOf(".", a + 1) + 1));
                String ICH;
                if (date < 10)
                    ICH = "0" + date + "-" + months[month - 1] + "-" + year;
                else
                    ICH = date + "-" + months[month - 1] + "-" + year;

                //new file name
                String newFileName;
                if (middle.isEmpty()) {
                    newFileName = "Xarelto" + "_" + last + ", " + first + "_ICH " + ICH + " GMS " + GMS + ".pdf";
                } else {
                    newFileName = "Xarelto" + "_" + last + ", " + first + " " + middle + "_ICH " + ICH + " GMS " + GMS + ".pdf";
                }
                File newFile = new File(dir, newFileName);
                if (i.renameTo(newFile)) {
                    renamed++;
                }else{

                }
            }
        };
        System.out.println(renamed+" out of "+files.length+" files successfully renamed");
    }

    private static void Risperdal_MBox(File[] files, String ICH){
        int renamed = 0;
        int n=1;
        for(File i: files){
            String name = i.getName();
            if(!name.startsWith("Risperdal")) {
            /*String last = name.substring(0, name.indexOf(','));
            String first = name.substring(name.indexOf(',')+1, name.indexOf('_'));
            String patientname = last+", "+first;*/
                String patientname = name.substring(0, name.indexOf("_ICH"));
                System.out.println(n + ".\t" + patientname);
                n++;
                String newFileName;
                if (name.contains("Amnd PFS")) {
                    newFileName = "Risperdal" + "_" + patientname + "_ICH " + ICH + " GMS " + GMS + " PFS Amnd.pdf";
                } else {
                    newFileName = "Risperdal" + "_" + patientname + "_ICH " + ICH + " GMS " + GMS + ".pdf";
                }
                File newFile = new File(dir, newFileName);
                if (i.renameTo(newFile)) {
                    renamed++;
                } else {  //if there are multiple files for one patient
                    //need to accomodate if there are two patients with same name
                    int p = 1;
                    String newFilePartName = newFileName;
                    while (!i.renameTo(new File(dir, newFilePartName))) {
                        p = p + 1;
                        newFilePartName = newFileName;
                        newFilePartName = newFileName.substring(0, newFileName.length() - 4) + "_Part" + p + ".pdf";
                    }
                    renamed++;
                }
            }
        }
        System.out.println(renamed+" out of "+files.length+" files successfully renamed");
    }

    /**
     * Steps before using this method:
     * 1. Right-click on zip file and click "Extract all files"
     * 2. Give the new folder a 1-word name because it is easier to type in when asked for folder name
     *
     * @param files, @param ICH
     */
    private static void Levaquin(File[] files, String ICH){
        int renamed=0;
        int total=0;
        for(File i: files) {
            String name = i.getName();   //patient name
            File[] infiles = i.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    File x = new File(dir, name);
                    return x.length()<=10000000;
                }
            }); //actual patient documents
            String newFileName;
            int p=1;
            Arrays.sort(infiles, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    int n1 = partNum(o1.getName());
                    int n2 = partNum(o2.getName());
                    return n1-n2;
                }
                private int partNum(String name){
                    try{
                        int n = name.indexOf("_Part");
                        int part = new Integer(name.substring(n+5, name.indexOf(".pdf")));
                        return part;
                    }catch(Exception e){
                        return 0;
                    }
                }
            });
            if(infiles.length==1){
                newFileName = "Levaquin_"+name+"_ICH "+ICH+" GMS "+GMS+".pdf";
                File newFile = new File(dir, newFileName);
                if (infiles[0].renameTo(newFile)) {
                    renamed++;
                }
                total++;
            }else {
                for (File j : infiles) {
                    System.out.println(j.getName());
                    newFileName = "Levaquin_"+name+"_ICH "+ICH+" GMS "+GMS+"_Part"+p+" of "+(infiles.length)+".pdf";
                    p++;
                    File newFile = new File(dir, newFileName);
                    if (j.renameTo(newFile)) {
                        renamed++;
                        System.out.println(newFileName);
                    }
                    total++;
                }
            }
        }
        System.out.println(renamed+" files renamed");
    }

    private static void Risperdal(File[] files){
        int renamed = 0;
        int n=1;
        Scanner sc = new Scanner(System.in);
        for(File i: files){
            String name = i.getName();
            if(!name.startsWith("Risperdal")){
                String ICH;
                String patientname;
                if (name.startsWith("2")) {
                    int m = Integer.parseInt(name.substring(5, 7));
                    ICH = name.substring(8, 10) + "-" + months[m - 1] + "-" + name.substring(0, 4);
                    try {
                        patientname = name.substring(11, name.indexOf('_', 11));
                    } catch (StringIndexOutOfBoundsException e) {
                        patientname = name.substring(11, name.length() - 4);
                    }
                    System.out.println(n+".\t"+patientname); n++;
                } else {
                    String lastname = name.substring(0, name.lastIndexOf(' '));
                    System.out.print("Patient name: " + lastname + ", ");
                    String firstname = sc.nextLine();
                	int k = name.indexOf(' ');
                	/*String firstname = name.substring(0, name.indexOf(' '));
                	String middle = " "+name.substring(k + 1, name.indexOf(' ', k + 1));
                    String lastname;
                    k = name.indexOf(' ', k + 1);
                    if (Character.isDigit(name.charAt(k + 1))) {  //no middle name
                        lastname = middle;
                        middle = "";
                    } else {  //is last name
                        lastname = name.substring(k + 1, name.indexOf(' ', k + 1));
                        k=name.lastIndexOf(' ');
                    }*/
                    patientname = lastname + ", " + firstname;
                    int j = name.indexOf('-');
                    int month = new Integer(name.substring(k+1, name.indexOf('-')));
                    int date = new Integer(name.substring(j + 1, name.indexOf('-', j + 1)));
                    String yr = name.substring(name.lastIndexOf("-") + 1,name.indexOf(".pdf"));
                    if (date < 10)
                        ICH = "0" + date + "-" + months[month - 1] + "-20" + yr;
                    else
                        ICH = name.substring(j + 1, name.indexOf('-', j + 1)) + "-" + months[month - 1] + "-20" + yr;
                }
                String newFileName;
                if (name.contains("Amnd PFS")) {
                    newFileName = "Risperdal" + "_" + patientname + "_ICH " + ICH + " GMS " + GMS + "_PFS Amnd.pdf";
                } else {
                    newFileName = "Risperdal" + "_" + patientname + "_ICH " + ICH + " GMS " + GMS + ".pdf";
                }
                if (name.contains("POI_POU")) {
                    newFileName = newFileName.substring(0, newFileName.indexOf(".pdf")) + "_POI_POU.pdf";
                }
                File newFile = new File(dir, newFileName);
                if (i.renameTo(newFile)) {
                    renamed++;
                } else {  //if there are multiple files for one patient
                    //need to accomodate if there are two patients with same name
                    int p = 1;
                    String newFilePartName = newFileName;
                    while (!i.renameTo(new File(dir, newFilePartName))) {
                        p = p + 1;
                        newFilePartName = newFileName;
                        newFilePartName = newFileName.substring(0, newFileName.length() - 4) + "_Part" + p + ".pdf";
                    }
                    renamed++;
                }
            }
        }
        System.out.println("\n"+renamed+" out of "+files.length+" files successfully renamed");
    }

    /**
     * These files come in zip files that contain filetree as follows:
     * Law Firm
     *  Name
     *      Documents
     *      Coversheet
     * Insert cover sheet into every document
     * Split every file larger than 13,000 kb only if there is 1 file over 13,000 kb. if there is more than one, leave it alone and rename/split manually and number it with renameSplitFiles()
     * Problem1: arrays are filled in alphabetical order so numbering of files will be wrong (e.g. part10 comes after part1, need part2 to come after part1)
     * How do i automate renaming in folders with two files greater than 13 MB?
     * @param files
     */
    private static void Invokana(File[] files, String ICH){
        int renamed = 0;
        int total = 0;
        Scanner sc = new Scanner(System.in);
        for(File i: files){
            String pname = i.getName(), name;
            
            if(!pname.startsWith("Invokana")) {
                if (i.isDirectory()) {
                	name = pname.charAt(0)+pname.substring(1, pname.indexOf(" ")).toLowerCase()+", "+pname.substring(pname.indexOf(" ")+1);
                    File[] infiles = i.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            File x = new File(dir, name);
                            return x.length() <= 13000000;
                        }
                    });
                    //file names have naming convention "# Name"
                    int p = 1;
                    Arrays.sort(infiles, new Comparator<File>() {
                        @Override
                        public int compare(File o1, File o2) {
                            int n1 = partNum(o1.getName());
                            int n2 = partNum(o2.getName());
                            return n1-n2;
                        }
                        private int partNum(String name){
                            try{
                                int n = name.indexOf("_Part");
                                int part = new Integer(name.substring(n+5, name.indexOf(".pdf")));
                                return part;
                            }catch(Exception e){
                                return 0;
                            }
                        }
                    });
                    for (File j : infiles) {
                    	
                        String newFileName = "Invokana" + "_" + name + "_ICH " + ICH + " GMS " + GMS + "_Part" + p + " of " + (infiles.length) + ".pdf";
                        System.out.println(j.getName());
                        System.out.println(newFileName);
                        p++;
                        File newFile = new File(dir, newFileName);
                        if (j.renameTo(newFile)) {
                            renamed++;
                        }
                        total++;
                    }
                }else{
                    String lastname = pname.charAt(0) + pname.substring(1, pname.indexOf(" ")).toLowerCase();
                    System.out.print("Patient name: " + lastname + ", ");
                    String firstname = sc.nextLine();
                    String patientname = lastname + ", " + firstname;
                    String newFileName = "Invokana_" + lastname + ", " + firstname + "_ICH " + ICH + " GMS " + GMS + ".pdf";
                    if (i.renameTo(new File(dir, newFileName))) {
                        renamed++;
                    }
                }
                
            }
        }
        System.out.println("Renamed: "+renamed+" out of "+total);
    }

    /**
     * apparently this doesn't work if any other pdf file is open??? What the fuck
     * @param files
     * @param ICH
     */
    private static void GoMarkers(File[] files, String ICH){
        int renamed = 0;
        int total = 0;
        int n = 1;
        for(File i: files){
            String name = i.getName();
            System.out.println(n+".\t"+name); n++;
            File[] infiles = i.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    File x = new File(dir, name);
                    return x.length()<=13000000 && name.endsWith(".pdf") && !name.contains("JBCS");
                }
            });
            /*Arrays.sort(infiles, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    int n1 = partNum(o1.getName());
                    int n2 = partNum(o2.getName());
                    return n1-n2;
                }
                private int partNum(String name){
                    try{
                        int n = name.indexOf("_Part");
                        int part = new Integer(name.substring(n+5, name.indexOf(".pdf")));
                        return part;
                    }catch(Exception e){
                        return 0;
                    }
                }
            });*/
            int p=1;
            String newFileName;
            if(infiles.length==1){
                //System.out.println("\t"+infiles[0].getName());
                newFileName = "Xarelto"+"_"+name+"_ICH "+ICH+" GMS "+GMS+".pdf";
                File newFile = new File(dir, newFileName);
                if (infiles[0].renameTo(newFile)) {
                    //System.out.println("\t"+newFileName);
                    renamed++;
                }
                total++;
            }else {
                for (File j : infiles) {
                    //System.out.println("\t"+j.getName());
                    newFileName = "Xarelto_"+name+"_ICH "+ICH+" GMS "+GMS+"_Part"+p+" of "+(infiles.length)+".pdf";
                    p++;
                    File newFile = new File(dir, newFileName);
                    if (j.renameTo(newFile)) {
                        //System.out.println("\t"+newFileName);
                        renamed++;
                    }
                    total++;
                }
            }
        }
        System.out.println("\nRenamed: "+renamed+" out of "+total);
    }

    private static void RenameFiles(File[] files, String ICH){
        Scanner sc = new Scanner(System.in);
        System.out.print("Drug: ");
        String drug = sc.next();
        int renamed = 0;
        for(File i: files) {
            System.out.println(i.getName());
            System.out.println("Name (Enter as Last, First): ");
            String name = sc.nextLine();
            String newFileName = drug+"_"+name+"_ICH "+ICH+" GMS "+GMS+".pdf";
            System.out.println(newFileName);
            File newFile = new File(dir, newFileName);
            if (i.renameTo(newFile)) {
                renamed++;
            }else {  //if there are multiple files for one patient
                //need to accomodate if there are two patients with same name
                int p = 1;
                String newFilePartName = newFileName;
                while (!i.renameTo(new File(dir, newFilePartName))) {
                    p = p + 1;
                    newFilePartName = newFileName;
                    newFilePartName = newFileName.substring(0, newFileName.length() - 4) + "_Part" + p + ".pdf";
                }
                renamed++;
            }
        }
        System.out.println(renamed + " out of " + files.length + " files successfully renamed");
    }

    private static void CountPatients(File[] files){
        int j=1;
        for(int i=0; i<files.length; i++){
            String name = files[i].getName();
            int k = name.indexOf('_');
            String last = name.substring(k+1, name.indexOf(','));
            String first = name.substring(name.indexOf(',')+2,name.indexOf("_ICH ",k+1));
            String ICH = name.substring(name.indexOf("ICH")+4, name.indexOf("GMS")-1);
            if(i==0 || !files[i-1].getName().contains(last+", "+first)){
                System.out.println(j+".\t"+first+" "+last);
                j++;
            }
        }
    }

    /**
     * ask for user to input number of files it split into in order to control function
     * many times the size of each split file will differ based on compression, number of files being split at once, etc
     *
     * @param files are all the files in December?
     */
    private static void RenameSplitFiles(File[] files){
        Scanner sc = new Scanner(System.in);
        int o=0, s=0;
        for(int i=0; i<files.length; i++){
            String name = files[i].getName();
            if(!name.endsWith("_Original.pdf") && !name.contains("_Part")) {
                long size = files[i].length();
                if (size > 9000000) {
                    System.out.println(name);
                    String original;
                    if (name.contains(".PDF")) {
                        original = name.substring(0, name.indexOf(".PDF")) + "_Original.pdf";
                    } else {
                        original = name.substring(0, name.indexOf(".pdf")) + "_Original.pdf";
                    }
                    if (files[i].renameTo(new File(dir, original))) o++;
                    System.out.print("How many parts is the document split into? ");
                    int parts = sc.nextInt();
                    //int parts = (int)(size/8000000);
                    for (int j = i + 1; j <= i + parts; j++) {
                        //get name of file part
                        String filename = files[j].getName();
                        //edit file name
                        String newFileName = filename.substring(0, filename.indexOf(".pdf")) + " of " + parts + ".pdf";
                        //rename split files
                        if (files[j].renameTo(new File(dir, newFileName))) s++;
                    }
                    i = i + parts;
                }
            }
        }
        System.out.println(o+" large files, "+s+" split files");
    }

    private static String GMSDate(){
        String month = months[c.get(c.MONTH)];
        if(c.get(c.DATE)<10)
            return "0"+c.get(c.DATE)+"-"+months[c.get(c.MONTH)]+"-"+c.get(c.YEAR);
        else
            return c.get(c.DATE)+"-"+months[c.get(c.MONTH)]+"-"+c.get(c.YEAR);
    }

    private static String GMSDate(String d){
        return d;
    }
}
