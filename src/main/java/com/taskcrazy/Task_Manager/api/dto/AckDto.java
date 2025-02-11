package com.taskcrazy.Task_Manager.api.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AckDto {


    private boolean answer;

    public static AckDto makeDefault(Boolean answer) {
        return builder()
                .answer(answer)
                .build();
    }
}
