/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package idh14.client;

import java.io.Serializable;

/**
 *
 * @author luche
 */
public class NewFileHandler implements Serializable{

    private String fileName;
    private String originalChecksum;
    
    public NewFileHandler(String fileName, String originalChecksum){
        this.fileName = fileName;
        this.originalChecksum = originalChecksum;
    }

    public String getFileName() {
        return fileName;
    }

    public String getOriginalChecksum() {
        return originalChecksum;
    }
    
  
}
