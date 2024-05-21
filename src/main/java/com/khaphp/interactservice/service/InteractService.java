package com.khaphp.interactservice.service;


import com.khaphp.common.dto.ResponseObject;
import com.khaphp.interactservice.dto.interact.InteractDTOcreate;

public interface InteractService {
    ResponseObject<Object> create(InteractDTOcreate object);
    ResponseObject<Object> delete(String id);
}
