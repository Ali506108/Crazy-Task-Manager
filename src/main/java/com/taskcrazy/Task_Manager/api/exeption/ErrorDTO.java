package com.taskcrazy.Task_Manager.api.exeption;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {


    private String error;

    @JsonProperty("error_description")
    private String errorDescription;


}
