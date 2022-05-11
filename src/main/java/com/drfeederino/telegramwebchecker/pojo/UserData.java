package com.drfeederino.telegramwebchecker.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserData {

    private Long id;
    private String number;
    private String barcode;
    private String lastStatus;

}
