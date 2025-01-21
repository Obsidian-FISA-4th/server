package ExamBattle.demo.service.query;

import org.springframework.stereotype.Service;

import ExamBattle.demo.apiPayload.code.status.ErrorStatus;
import ExamBattle.demo.apiPayload.exception.handler.TempHandler;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class TempQueryServiceImpl implements TempQueryService{

    @Override
    public void CheckFlag(Integer flag) {
        if (flag == 1)
            throw new TempHandler(ErrorStatus.TEMP_EXCEPTION);
    }
}
