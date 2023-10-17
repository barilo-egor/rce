package tgb.btc.rce.service.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.bean.web.api.ApiUser;
import tgb.btc.library.bean.web.api.UsdApiUserCourse;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.repository.web.ApiUserRepository;
import tgb.btc.library.repository.web.UsdApiUserCourseRepository;
import tgb.btc.rce.web.vo.ApiUserVO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ApiUserProcessService {

    private ApiUserRepository apiUserRepository;

    private UsdApiUserCourseRepository usdApiUserCourseRepository;

    @Autowired
    public void setUsdApiUserCourseRepository(UsdApiUserCourseRepository usdApiUserCourseRepository) {
        this.usdApiUserCourseRepository = usdApiUserCourseRepository;
    }

    @Autowired
    public void setApiUserRepository(ApiUserRepository apiUserRepository) {
        this.apiUserRepository = apiUserRepository;
    }

    public ApiUser save(ApiUserVO apiUserVO) {
        ApiUser apiUser;
        if (Objects.nonNull(apiUserVO.getPid())) {
            apiUser = apiUserRepository.getById(apiUserVO.getPid());
            apiUser.setIsBanned(apiUserVO.getIsBanned());
        } else {
            apiUser = new ApiUser();
            apiUser.setRegistrationDate(LocalDate.now());
            apiUser.setIsBanned(false);
            String token = RandomStringUtils.randomAlphanumeric(40);
            while (apiUserRepository.countByToken(token) > 0) {
                token = RandomStringUtils.randomAlphanumeric(40);
            }
            apiUser.setToken(token);
            List<UsdApiUserCourse> usdApiUserCourseList = new ArrayList<>();
            if (Objects.nonNull(apiUserVO.getUsdCourseBYN())) {
                usdApiUserCourseList.add(usdApiUserCourseRepository.save(UsdApiUserCourse.builder()
                        .fiatCurrency(FiatCurrency.BYN)
                        .course(apiUserVO.getUsdCourseBYN())
                        .build()));
            }
            if (Objects.nonNull(apiUserVO.getUsdCourseRUB())) {
                usdApiUserCourseList.add(usdApiUserCourseRepository.save(UsdApiUserCourse.builder()
                        .fiatCurrency(FiatCurrency.BYN)
                        .course(apiUserVO.getUsdCourseRUB())
                        .build()));
            }
            apiUser.setUsdApiUserCourseList(usdApiUserCourseList);
        }
        if (Objects.nonNull(apiUserVO.getUsdCourseBYN())) {
            UsdApiUserCourse byn = apiUser.getCourse(FiatCurrency.BYN);
            if (Objects.isNull(byn)) {
                UsdApiUserCourse usdApiUserCourse = usdApiUserCourseRepository.save(UsdApiUserCourse.builder()
                        .fiatCurrency(FiatCurrency.BYN)
                        .course(apiUserVO.getUsdCourseBYN())
                        .build());
                apiUser.getUsdApiUserCourseList().add(usdApiUserCourse);
            } else {
                byn.setCourse(apiUserVO.getUsdCourseBYN());
                usdApiUserCourseRepository.save(byn);
            }
        }
        if (Objects.nonNull(apiUserVO.getUsdCourseRUB())) {
            UsdApiUserCourse rub = apiUser.getCourse(FiatCurrency.RUB);
            if (Objects.isNull(rub)) {
                UsdApiUserCourse usdApiUserCourse = usdApiUserCourseRepository.save(UsdApiUserCourse.builder()
                        .fiatCurrency(FiatCurrency.RUB)
                        .course(apiUserVO.getUsdCourseRUB())
                        .build());
                apiUser.getUsdApiUserCourseList().add(usdApiUserCourse);
            } else {
                rub.setCourse(apiUserVO.getUsdCourseRUB());
                usdApiUserCourseRepository.save(rub);
            }
        }
        apiUser.setId(apiUserVO.getId());
        apiUser.setPersonalDiscount(apiUserVO.getPersonalDiscount());
        apiUser.setBuyRequisite(apiUserVO.getBuyRequisite());
        apiUser.setSellRequisite(apiUserVO.getSellRequisite());
        apiUser.setFiatCurrency(apiUserVO.getFiatCurrency());
        return apiUserRepository.save(apiUser);
    }
}
