package com.khaphp.interactservice.service;

import com.khaphp.common.dto.ResponseObject;
import com.khaphp.common.dto.noti.NotificationDTOcreate;
import com.khaphp.common.entity.CookingRecipe;
import com.khaphp.common.entity.UserSystem;
import com.khaphp.interactservice.call.foodrecipeservice.FoodRecipeServiceCall;
import com.khaphp.interactservice.call.notiservice.NotiServiceCall;
import com.khaphp.interactservice.call.userservice.UserServiceCall;
import com.khaphp.interactservice.constant.TypeInteract;
import com.khaphp.interactservice.dto.interact.InteractDTOcreate;
import com.khaphp.interactservice.entity.Interact;
import com.khaphp.interactservice.exception.ObjectNotFound;
import com.khaphp.interactservice.repo.InteractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InteractServiceImpl implements InteractService {
    public static final String USER_NOT_FOUND_MSG = "user not found";
    public static final String YOU_HAVE_ALREADY_DONE_IT_MSG = "you have already done it";
    private final UserServiceCall userServiceCall;
    private final FoodRecipeServiceCall foodRecipeServiceCall;
    private final NotiServiceCall notiServiceCall;
    private final InteractRepository interactRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject<Object> create(InteractDTOcreate object) {
        try{
            UserSystem userSystem = userServiceCall.getObject(object.getCustomerId());
            if(userSystem == null){
                throw new ObjectNotFound(USER_NOT_FOUND_MSG);
            }

            CookingRecipe cookingRecipe = foodRecipeServiceCall.getDetail(object.getCookingRecipeId());
            if(cookingRecipe == null){
                throw new ObjectNotFound("cookingRecipe not found");
            }

            List<String> gmailAlreadyShare = null;
            if(object.getTypeInteract().equals(TypeInteract.LIKE.toString())){
                Interact like = new Interact();
                like.setType(TypeInteract.LIKE);
                like.setCustomerId(userSystem.getId());
                like.setCookingRecipeId(cookingRecipe.getId());
                if(!interactRepository.existsByCustomerIdAndCookingRecipeIdAndType(
                        userSystem.getId(), cookingRecipe.getId(), TypeInteract.LIKE)){
                    interactRepository.save(like);
                }else{
                    throw new Exception(YOU_HAVE_ALREADY_DONE_IT_MSG);
                }

            }else if(object.getTypeInteract().equals(TypeInteract.SHARE.toString())){
                //kiểm tra chỉnh chủ mới đc quyền share
                if(!userSystem.getId().equals(cookingRecipe.getCustomerId())){
                    throw new Exception("you can't share this recipe because you don't own it");
                }
                if(object.getGmails().size() > 0){
                    gmailAlreadyShare = new ArrayList<>();
                    for(String gmail : object.getGmails()){
                        UserSystem user = userServiceCall.getDetailByEmail(gmail);
                        if(user == null){
                            throw new ObjectNotFound("user with gmail "+gmail+" not found");
                        }

                        Interact share = new Interact();
                        share.setType(TypeInteract.SHARE);
                        share.setCustomerId(user.getId());
                        share.setCookingRecipeId(cookingRecipe.getId());

                        if(!interactRepository.existsByCustomerIdAndCookingRecipeIdAndType(
                                user.getId(), cookingRecipe.getId(), TypeInteract.SHARE)){
                            interactRepository.save(share);
                        }else{
                            gmailAlreadyShare.add(gmail);
                        }
                    }
                }
            }else if(object.getTypeInteract().equals(TypeInteract.VOTE.toString())){
                Interact votes = null;

                //check xem đã vote chưa, nếu vote rồi thì chỉ cập nhật lại số star
                if(interactRepository.existsByCustomerIdAndCookingRecipeIdAndType(
                        userSystem.getId(), cookingRecipe.getId(), TypeInteract.VOTE)){
                    votes = interactRepository.findByCustomerIdAndCookingRecipeIdAndType(
                            userSystem.getId(), cookingRecipe.getId(), TypeInteract.VOTE);
                    votes.setStar(object.getStar());
                }else{
                    votes = new Interact();
                    votes.setCookingRecipeId(cookingRecipe.getId());
                    votes.setCustomerId(userSystem.getId());
                    votes.setStar(object.getStar());
                    votes.setType(TypeInteract.VOTE);
                }
                interactRepository.save(votes);

            }else if(object.getTypeInteract().equals(TypeInteract.REPORT.toString())){
                Interact report = new Interact();
                report.setCookingRecipeId(cookingRecipe.getId());
                report.setCustomerId(userSystem.getId());
                report.setType(TypeInteract.REPORT);

                if(!interactRepository.existsByCustomerIdAndCookingRecipeIdAndType(
                        userSystem.getId(), cookingRecipe.getId(), TypeInteract.REPORT)){
                    interactRepository.save(report);
                }else{
                    throw new Exception(YOU_HAVE_ALREADY_DONE_IT_MSG);
                }
            }

            //tạo notification cho owner cooking recipe
            try{
                if(object.getTypeInteract().equals(TypeInteract.LIKE.toString())){
                    notiServiceCall.create(NotificationDTOcreate.builder()
                            .userId(cookingRecipe.getCustomerId())
                            .title(userSystem.getName() +" đã thích công thức " + cookingRecipe.getName())
                            .build());
                } else if(object.getTypeInteract().equals(TypeInteract.SHARE.toString())){ //noti cho người đc share công thức này
                    for(String gmail : object.getGmails()){
                        if(gmailAlreadyShare != null && gmailAlreadyShare.contains(gmail)){
                            continue;
                        }
                        UserSystem user = userServiceCall.getDetailByEmail(gmail);
                        UserSystem ownerRecipe = userServiceCall.getObject(cookingRecipe.getCustomerId());
                        notiServiceCall.create(NotificationDTOcreate.builder()
                                .userId(user.getId())
                                .title(ownerRecipe.getName() +" đã chia sẻ công thức "+cookingRecipe.getName() + " với bạn")
                                .build());
                    }
                }
            }catch (Exception ex){
                log.error("Exception when notification for user: " + ex.getMessage());
            }
            return ResponseObject.builder()
                    .code(200)
                    .message("Success " + object.getTypeInteract())
                    .build();
        }catch (Exception e){
            return ResponseObject.builder()
                    .code(400)
                    .message("Exception: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseObject<Object> delete(String id) {
        try{
            if(interactRepository.existsById(id)){
                interactRepository.deleteById(id);
            }else{
                throw new ObjectNotFound("Interact not found");
            }
            return ResponseObject.builder()
                    .code(200)
                    .message("Success")
                    .build();
        }catch (Exception e){
            return ResponseObject.builder()
                    .code(400)
                    .message("Exception: " + e.getMessage())
                    .build();
        }
    }
}
