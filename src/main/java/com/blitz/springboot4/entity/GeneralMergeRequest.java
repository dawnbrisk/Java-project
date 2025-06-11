package com.blitz.springboot4.entity;

import java.util.List;

public class GeneralMergeRequest {
    private String palletA_code;
    private String palletB_code;
    private List<String> fileUrls;

    // Getter 和 Setter
    public String getPalletA_code() {
        return palletA_code;
    }

    public void setPalletA_code(String palletA_code) {
        this.palletA_code = palletA_code;
    }

    public String getPalletB_code() {
        return palletB_code;
    }

    public void setPalletB_code(String palletB_code) {
        this.palletB_code = palletB_code;
    }

    public List<String> getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(List<String> fileUrls) {
        this.fileUrls = fileUrls;
    }
}
