/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cm;

/**
 *
 * @author raushan
 */
class Flight {
    String ActualArrTime;
    String ActualDepTime;
    String ArrTime;
    String DepTime;
    String EstimatedArrTime;
    String EstimatedDepTime;
    
    String cityFromCode;
    String cityToCode;
    String cityFromName;
    String cityToName;
    
    String StrArrStatusTranslation;    
    String StrDepStatusTranslation;
    
    Boolean trackingAvailable;
    String flightNumber;
    
    public Flight(String ActualArrTime, String ActualDepTime, String ArrTime, String DepTime, String EstimatedArrTime, String EstimatedDepTime, String cityFromCode, String cityToCode, String cityFromName, String cityToName, String StrArrStatusTranslation, String StrDepStatusTranslation, Boolean trackingAvailable, String FlightNumber){
        this.ActualArrTime = ActualArrTime;
        this.ActualDepTime = ActualDepTime;
        this.ArrTime = ArrTime;
        this.DepTime = DepTime;
        this.EstimatedArrTime = EstimatedArrTime;
        this.EstimatedDepTime = EstimatedDepTime;

        this.cityFromCode = cityFromCode;
        this.cityToCode = cityToCode;
        this.cityFromName = cityFromName;
        this.cityToName = cityToName;

        this.StrArrStatusTranslation = StrArrStatusTranslation;    
        this.StrDepStatusTranslation = StrDepStatusTranslation;

        this.trackingAvailable = trackingAvailable;
        this.flightNumber = FlightNumber;
    }
    
    @Override
    public String toString(){        
        return "\nНомер рейса: " + flightNumber + "\n" + cityFromName + "(" +cityFromCode+")" + " - " + cityToName  + "(" + cityToCode + ")" + "\nВремя вылета: " + DepTime + "(" + StrDepStatusTranslation + ")" + "\nВремя прилета: " + ArrTime + "(" + StrArrStatusTranslation + ")" + "\n";
    }
}
