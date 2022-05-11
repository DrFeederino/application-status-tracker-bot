package com.drfeederino.telegramwebchecker.entities;

import com.drfeederino.telegramwebchecker.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "telegram_user")
@AllArgsConstructor
@NoArgsConstructor
public class TelegramUser {

    @Id
    Long id;
    UserStatus status;
    String number;
    String barcode;
    @Lob
    String lastStatus;

}
