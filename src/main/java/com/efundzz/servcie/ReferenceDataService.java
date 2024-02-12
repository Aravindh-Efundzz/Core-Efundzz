package com.efundzz.servcie;//package com.efundzz.servcie;
//
//import com.efundzz.DTO.ReferenceDataDTO;
//import com.efundzz.entity.Reference;
//import com.efundzz.repository.ReferenceDataRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Component
//@RequiredArgsConstructor
//public class ReferenceDataService {
//    public final ReferenceDataRepository referenceDataRepository;
//
//    public List<ReferenceDataDTO> findDataByRefKey1(String refKey1) {
//        try {
//            return referenceDataRepository.findByRefKey1(refKey1).stream().map(this::fetchReferenceDataDTO).collect(Collectors.toList());
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public List<ReferenceDataDTO> findDataByRefKey1AndRefKey2(String refKey1, String refKey2) {
//        try {
//            return referenceDataRepository.findByRefKey1AndRefKey2(refKey1, refKey2).stream().map(this::fetchReferenceDataDTO).collect(Collectors.toList());
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public ReferenceDataDTO fetchReferenceDataDTO(Reference ref) {
//        try {
//            return ReferenceDataDTO.builder()
//                    .id(ref.getKey()).name(ref.getValue())
//                    .image(ref.getImage()).enabled(ref.getIsEnabled().equals("Y")).order(ref.getOrder())
//                    .ref_key1(ref.getRefKey1()).ref_key2(ref.getRefKey2()).ref_key3(ref.getRefKey3()).ref_key4(ref.getRefKey4())
//                    .build();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}
