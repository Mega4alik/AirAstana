/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml;

import com.google.gson.Gson;
import global.Global;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.json.JSONObject;
import smile.classification.RandomForest;
import smile.classification.DecisionTree;
import smile.classification.SVM;
import smile.data.NominalAttribute;
import smile.math.Math;
import smile.math.kernel.LinearKernel;
import smile.math.kernel.GaussianKernel;

public class Classifier {
    Gson gson = new Gson();
    Global g = new Global();    
    int QAClassesN = 500;
    static boolean initialized = false;    
    static SVM<double[]> modelSeparator, modelGD, modelQA;    
    //static DecisionTree modelSeparator, modelGD, modelQA; //RandomForest 
    
    public static void main(String args[]) throws Exception {
        Classifier cl = new Classifier();
        cl.train();        
    }
    
    public Classifier() throws Exception{
        if (!initialized){
            initialized = true;
            load();
        }
    }
            
    public JSONObject getQuestionIdx(double[] x, double[] xQA) throws Exception {                                             
        int qIdx;
        JSONObject obj = new JSONObject();   
        int separator = modelSeparator.predict(x) + 1;//0-QA, 1-GD                
        obj.put("mode", separator);
        double[] prob ;//= new double[322];        
        if (separator == 1){
            prob = new double[300];
            qIdx = modelQA.predict(xQA, prob);            
            System.out.println("getQuestionIdx.QA.Prob = "+prob[qIdx]+" idx = "+qIdx);  
            //if (prob[qIdx] < 0.01) qIdx = -1;                      
        } else {
            prob = new double[250];
            qIdx = modelGD.predict(x, prob);         
            //System.out.println("getQuestionIdx.GD.Prob = "+prob[qIdx]+" idx = "+qIdx);  
            //if (Math.max(x) == 0) qIdx = -1; //All words Are unknown
            //else if (prob[qIdx] < 0.01) qIdx = -1; 
            //qIdx = modelGD.predict(x);         
        }
        obj.put("prob",prob[qIdx]);
        obj.put("qIdx", qIdx);
        return obj;
            
        //python        
        //return pPredict(x, xQA);        
    }
    
    public JSONObject pPredict(double[] x, double[] xQA) throws Exception {
        JSONObject obj = new JSONObject();
        String x_st = "["+gson.toJson(x)+"]";
        String xQA_st = "["+gson.toJson(xQA)+"]";        
        String pythonExePath = (g.run_mode == 1 ? "/usr/local/bin/python3" : "/usr/bin/python");
        String projectPath = (g.run_mode==1? "/Users/admin/Desktop/python/ailabs/aicc/" : "/home/ubuntu/python/aicc/");
        String exeSt = pythonExePath+" "+projectPath+"main.py "+g.run_mode+" "+x_st+" "+xQA_st;   
        System.out.println(exeSt);
        Process p = Runtime.getRuntime().exec(exeSt);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream())); 
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        // read the output from the command        
        String s, ans = "";
        while ((s = stdInput.readLine()) != null) {ans+=s;}        
        System.out.println("Result = "+ans);        
        // read any errors from the attempted command        
        //while ((s = stdError.readLine()) != null) {}        
        stdInput.close();
        stdError.close();     
        
        String arr[]  = ans.split(",");
        obj.put("mode",Integer.parseInt(arr[0].trim()));
        obj.put("qIdx",Integer.parseInt(arr[1]));
        obj.put("prob",Double.parseDouble(arr[2]));
        
        return obj;
    }    
    
    void train() throws Exception {                
        System.out.println("SVM Training Started");        
        SVM<double[]> modelSeparatorNew, modelGDNew, modelQANew;    
        //DecisionTree modelSeparatorNew, modelGDNew, modelQANew;            
        for (String mn : new String[]{"QA","GD","Separator"}){
            double[][] X = gson.fromJson(g.getFileContent(g.Path+"/model/XTrain"+mn+".txt"), double[][].class);
            int[] Y = gson.fromJson(g.getFileContent(g.Path+"/model/YTrain"+mn+".txt"), int[].class);                                
            System.out.println(mn+"Model training. x size - "+X[0].length);
            if (mn.equals("QA")){                                                
                modelQANew = new SVM<double[]>(new LinearKernel(), 0.1, Math.max(Y)+1, SVM.Multiclass.ONE_VS_ALL);
                modelQANew.learn(X, Y);
                modelQANew.trainPlattScaling(X, Y);                                 
                //modelQANew = trainRF(X,Y);
                printModel(modelQANew,"modelQA");
            } else if (mn.equals("GD")){
                modelGDNew = new SVM<double[]>(new LinearKernel(), 0.1, Math.max(Y)+1, SVM.Multiclass.ONE_VS_ALL);
                modelGDNew.learn(X, Y);                
                modelGDNew.trainPlattScaling(X, Y);                                 
                //modelGDNew = trainRF(X,Y);
                printModel(modelGDNew,"modelGD");                
            } else if (mn.equals("Separator")){                
                modelSeparatorNew = new SVM<double[]>(new LinearKernel(), 1.0, 2); 
                modelSeparatorNew.learn(X, Y);
                //modelSeparatorNew = trainRF(X,Y);
                printModel(modelSeparatorNew,"modelSeparator");                
            }                                                                                        
        }
        System.out.println("SVM Training Finished");                                    
    }
       
    void load() throws Exception{                                          
        System.out.println("ML Model loading started");    
        FileInputStream fileIn;            
        ObjectInputStream in;            
        for (String modelName : new String[]{"modelSeparator","modelGD","modelQA"}){
            fileIn = new FileInputStream(g.Path+"/model/"+modelName+".txt");
            in = new ObjectInputStream(fileIn);
            if (modelName.equals("modelSeparator")) modelSeparator = (SVM<double[]>) in.readObject(); //(SVM<double[]>) RandomForest
            else if (modelName.equals("modelQA")) modelQA = (SVM<double[]>) in.readObject();            
            else if (modelName.equals("modelGD")) modelGD = (SVM<double[]>) in.readObject();
            in.close();
            fileIn.close();        
        }
        System.out.println("ML Model loading finished");                                       
    }
    
    DecisionTree trainRF(double[][] X, int[] Y) throws Exception {
        DecisionTree model = new DecisionTree(X,Y, 100);
        NominalAttribute ars[] = new NominalAttribute[X[0].length];
        String[] values = {"0","1"};                
        for (int i=0;i<ars.length;i++) ars[i] = new NominalAttribute("atr"+i,values);                
        //RandomForest model = new RandomForest(X, Y, 500, ); // 100, 5, 500, 1        
        return model;
    }
    
    
    void printModel(Object model, String name) throws Exception { //SVM<double[]> model
        FileOutputStream fileOut = new FileOutputStream(g.Path+"/model/"+name+".txt");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(model); 
        out.close();
        fileOut.close();                         
    }
    
}

