package com.seckill.web;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExcution;
import com.seckill.dto.SeckillResult;
import com.seckill.entity.Seckill;
import com.seckill.enums.SeckillStateEnum;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author raoke007
 * @date 2018/9/13
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        List<Seckill> seckillList = seckillService.getSeckillList();
        model.addAttribute("seckillList", seckillList);
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        Seckill seckill = seckillService.getById(seckillId);
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> now() {
        long now = new Date().getTime();
        return new SeckillResult<>(true, now);
    }

    @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody //会把 result 转换为json形式传给前端
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result;

        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<>(false, e.getMessage());
        }

        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExcution> execute(@PathVariable("seckillId") Long seckillId, @PathVariable("md5") String md5,
                                                  @CookieValue(value = "killPhone", required = false) Long userPhone) {
        SeckillResult<SeckillExcution> result;

        if (userPhone == null) {
            return new SeckillResult<>(false, "未注册手机号");
        } else {
            try {
                SeckillExcution excution = seckillService.executeSeckillPro(seckillId, userPhone, md5);
                result = new SeckillResult<>(true, excution);
            } catch (RepeatKillException e) {
                SeckillExcution excution = new SeckillExcution(seckillId, SeckillStateEnum.REPEAT_KILL);
                result = new SeckillResult<>(false, excution);
            } catch (SeckillCloseException e) {
                SeckillExcution excution = new SeckillExcution(seckillId, SeckillStateEnum.END);
                result = new SeckillResult<>(false, excution);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                SeckillExcution excution = new SeckillExcution(seckillId, SeckillStateEnum.INNER_ERROR);
                result = new SeckillResult<>(false, excution);
            }

            return result;
        }
    }
}
