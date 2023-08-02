import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainTest {

    public static void main(String [] arg) throws IOException {

        // example1
        // ASL_BU
        // AUslan2
        // Blocks

//        //debug version (single run)
//        String input = fileToPath("Blocks.txt");
//        String output = ".//output.txt";
//        double min_sup = 0.01;  //
//        int eposilon = 0;
//        int min_gap = 0;
//        int max_gap = 10000;
//        int min_dur = 1;
//        int max_dur = 10000;
//        // %%%%%%%%%%%%%%%%%%%%%%% improve1 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//        // for example, if TI = <2,4>, then t0 is 0, t1 is (0,2], t2 is (2,4], t3 is (4,~)
//        List<Integer> timeInt = new ArrayList<Integer>();
//        timeInt.add(20);
//        timeInt.add(40);
//        // %%%%%%%%%%%%%%%%%%%%%%% improve1 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//        AlgoVertTIRP vertTIRP = new AlgoVertTIRP();
//        vertTIRP.runAlgorithm(input,null, output, min_sup,timeInt,eposilon,min_gap,max_gap,min_dur,max_dur);
//        vertTIRP.printStats();

//         example1
//         ASL_BU
//         AUslan2
//         Blocks
//         db100000_item7_event6

//        // data version-varied dataset for paint (multi run)
//        // data version for paint (multi run)
//        List<Integer> variedPara = new ArrayList<>();
//        variedPara.add(10000);variedPara.add(20000);variedPara.add(50000);variedPara.add(80000);variedPara.add(100000);variedPara.add(150000);variedPara.add(200000);
//        int time = variedPara.size(); // times of running
//        double relaRelaminSup = 0.0003;
//        String paintOutput = "H:/PostGraduate/PAPER/2022/paper3/Figure/data.txt";     // data file (for paint)
//        boolean append = false; // decide if add new info into file or just rewrite it
//        boolean transform = true; // decide if transform data into final data, generally, it is false
//
//        // for example, if TI = <2,4>, then t0 is 0, t1 is (0,2], t2 is (2,4], t3 is (4,~)
//        List<Integer> timeInt = new ArrayList<Integer>();
//        timeInt.add(20);
//        timeInt.add(40);
//        int eposilon = 0;
//        int min_gap = 0;
//        int max_gap = 10000;
//        int min_dur = 1;
//        int max_dur = 10000;
//
//        BufferedWriter writerPaint = new BufferedWriter(new FileWriter(paintOutput,append));   // writer to write the data file (for paint)
//        StringBuilder buffer = new StringBuilder();
//        writerPaint.write("###");
//        writerPaint.newLine();
//        buffer.append("variedSize");
//        writerPaint.write(buffer.toString());
//        writerPaint.newLine();
//        writerPaint.write("type: TM");
//        writerPaint.newLine();
//        writerPaint.write("fixedThreshold: "+"7_200_0.0003");
//        writerPaint.newLine();
//        writerPaint.write("variedPara: "+"size");
//        writerPaint.newLine();
//
//        for (int i=0;i<time;i++){
//            AlgoVertTIRP algoVertTIRP = new AlgoVertTIRP();
//            String data = "db"+variedPara.get(i)+"_item200_event7.txt";
//            String input = fileToPath(data);
//            String output = "output.txt";
//            writerPaint.write("threshold: "+ variedPara.get(i));
//            writerPaint.newLine();
//            System.gc();  // 尽快进行垃圾回收
//            algoVertTIRP.runAlgorithm(input,writerPaint,output,relaRelaminSup,timeInt, eposilon,min_gap,max_gap,min_dur,max_dur);
//            algoVertTIRP.printStats();
//        }
//        writerPaint.close();
//
//        if (transform){
//            String data = "H:/PostGraduate/PAPER/2022/paper3/Figure/data.txt";
//            String finalData = "H:/PostGraduate/PAPER/2022/paper3/Figure/finalData.txt";     // data file (for paint)
//            transformdata(data,finalData);
//        }


        //         ASL_BU   ASL_BU_clean
        //         AUslan2  AUslan2_clean
        //         Blocks   Blocks_clean

        // data version for paint (multi run)
        int time = 7; // times of running
        String datasetName = "db50000_item100_event10.txt";  // name of database that is going to run
        String output = ".//output.txt";
        String paintOutput = "H:/PostGraduate/PAPER/2022/paper3/Figure/data.txt";     // data file (for paint)
        boolean append = true; // decide if add new info into file or just rewrite it
        boolean transform = true; // decide if transform data into final data, generally, it is false

        int eposilon = 0;
        int min_gap = 0;
        int max_gap = 10000;
        int min_dur = 1;
        int max_dur = 10000;
        // %%%%%%%%%%%%%%%%%%%%%%% improve1 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // for example, if TI = <2,4>, then t0 is 0, t1 is (0,2], t2 is (2,4], t3 is (4,~)
        List<Integer> timeInt = new ArrayList<Integer>();
        timeInt.add(20);
        timeInt.add(40);
        // %%%%%%%%%%%%%%%%%%%%%%% improve1 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        double startRelaminSup = 0.0005;;
        double endRelaminSup = 0.001;

        multiRun(paintOutput, output,append,datasetName, startRelaminSup,endRelaminSup,time,timeInt,eposilon,min_gap,max_gap,min_dur,max_dur);

        if (transform){
            String data = "H:/PostGraduate/PAPER/2022/paper3/Figure/data.txt";
            String finalData = "H:/PostGraduate/PAPER/2022/paper3/Figure/finalData.txt";     // data file (for paint)
            transformdata(data,finalData);
        }

    }

    /**
     * method to run algo muti times for store info for painting
     * @param paintOutput output data for paint
     * @param outputFile output file in java project for analyse
     * @param append decide if clear paintOutput or append it
     * @param datasetName
     * @param startRelaminSup
     * @param endRelaminSup
     * @param time the time that is going to run
     * @param eposilon
     * @param min_gap
     * @param max_gap
     * @param min_dur
     * @param max_dur
     * @throws IOException
     */
    private static void multiRun(String paintOutput, String outputFile, boolean append, String datasetName,
                                 double startRelaminSup, double endRelaminSup, int time,List<Integer> timeInt,
                                 int eposilon, int min_gap, int max_gap, int min_dur, int max_dur) throws IOException {
        BufferedWriter writerPaint = new BufferedWriter(new FileWriter(paintOutput,append));   // writer to write the data file (for paint)
        StringBuilder buffer = new StringBuilder();
        writerPaint.write("###");
        writerPaint.newLine();
        buffer.append(datasetName.substring(0,datasetName.length()-4));
        writerPaint.write(buffer.toString());
        writerPaint.newLine();
        writerPaint.write("type: TM");
        writerPaint.newLine();
        writerPaint.write("fixedThreshold: -");
        writerPaint.newLine();
        writerPaint.write("variedPara: minSup");
        writerPaint.newLine();
        double step = (endRelaminSup-startRelaminSup)/(time-1);
        for (int i=0;i<time;i++){
            AlgoVertTIRP algoVertTIRP = new AlgoVertTIRP();
            String data = datasetName;
            String input = fileToPath(data);
            String output = outputFile;
            double relaRelaminSup = startRelaminSup+step*i;
            writerPaint.write("threshold: "+ relaRelaminSup);
            writerPaint.newLine();
            algoVertTIRP.runAlgorithm(input,writerPaint, output, relaRelaminSup, timeInt, eposilon,min_gap,max_gap,min_dur,max_dur);
            algoVertTIRP.printStats();
        }
        writerPaint.close();
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTest.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }

    private static void transformdata(String paintOutput, String finalData) throws IOException {
        String oldFile = paintOutput;
        String newFile = finalData;
        BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));   // writer to write the data file (for paint)
        //read from .txt
        BufferedReader myInput = null;
        String thisLine = null;
        int line = 1;
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(oldFile))));
            thisLine = myInput.readLine();
            while (thisLine != null) {
                if (thisLine.equals("###")){
                    // write the first row
                    writer.write("# ");           // write #
                    thisLine = myInput.readLine();
                    writer.write(thisLine); // write database name
                    thisLine = myInput.readLine();
                    String split[] = thisLine.split(":");
                    if (split[0].equals("type")){
                        writer.write(split[1]);       // write type (correspond algorithm)
                    }else {
                        System.out.println("type wrong");
                    }
                    thisLine = myInput.readLine();
                    split = thisLine.split(":");
                    if (split[0].equals("fixedThreshold")){
                        writer.write(split[1]);       // write fixed thresholds (correspond algorithm)
                    }else {
                        System.out.println("fixed threshold wrong");
                    }
                    thisLine = myInput.readLine();
                    split = thisLine.split(":");
                    if (split[0].equals("variedPara")){
                        writer.write(split[1]);       // write variedPara (correspond algorithm)
                    }else {
                        System.out.println("variedPara wrong");
                    }
                    List<Double> threshold = new ArrayList<>();
                    List<Double> runtime_total = new ArrayList<>();
                    List<Double> runtime_pre = new ArrayList<>();
                    List<Double> runtime_process = new ArrayList<>();
                    List<Double> memory = new ArrayList<>();
                    List<Integer> count = new ArrayList<>();
                    while (thisLine!=null&&!(thisLine.equals("###"))){
                        split = thisLine.split(":");
                        if (split[0].equals("threshold")){
                            threshold.add(Double.valueOf(split[1]));
                        }
                        if (split[0].equals("results")){
                            runtime_total.add(Double.valueOf(split[1]));
                            runtime_pre.add(Double.valueOf(split[2]));
                            runtime_process.add(Double.valueOf(split[3]));
                            memory.add(Double.valueOf(split[4]));
                            count.add(Integer.valueOf(split[5]));
                        }
                        thisLine = myInput.readLine();
                    }
                    // write varied threshold
                    writer.newLine();
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("threshold ");
                    for (int i=0;i<threshold.size();i++){
                        buffer.append(threshold.get(i)+" ");
                    }
                    writer.write(buffer.toString());
                    // write total runtime
                    writer.newLine();
                    buffer = new StringBuilder();
                    buffer.append("totalRuntime ");
                    for (int i=0;i<threshold.size();i++){
                        buffer.append(runtime_total.get(i)+" ");
                    }
                    writer.write(buffer.toString());
                    // write pre runtime
                    writer.newLine();
                    buffer = new StringBuilder();
                    buffer.append("preRuntime ");
                    for (int i=0;i<threshold.size();i++){
                        buffer.append(runtime_pre.get(i)+" ");
                    }
                    writer.write(buffer.toString());
                    // write process runtime
                    writer.newLine();
                    buffer = new StringBuilder();
                    buffer.append("processRuntime ");
                    for (int i=0;i<threshold.size();i++){
                        buffer.append(runtime_process.get(i)+" ");
                    }
                    writer.write(buffer.toString());
                    // write memory
                    writer.newLine();
                    buffer = new StringBuilder();
                    buffer.append("memory ");
                    for (int i=0;i<threshold.size();i++){
                        buffer.append(memory.get(i)+" ");
                    }
                    writer.write(buffer.toString());
                    // write count
                    writer.newLine();
                    buffer = new StringBuilder();
                    buffer.append("count ");
                    for (int i=0;i<threshold.size();i++){
                        buffer.append(count.get(i)+" ");
                    }
                    writer.write(buffer.toString());
                    writer.newLine();
                }
            }
        }catch (Exception e) {
            // catches exception if error while reading the input file
            e.printStackTrace();
        }finally {
            if(myInput != null){
                myInput.close();
            }
        }
        writer.close();
    }

}
