-- 定义存储过程
DELIMITER $$

CREATE PROCEDURE `seckill`.`execute_seckill`
  (in v_seckill_id BIGINT, in v_phone BIGINT,
    in v_kill_time TIMESTAMP, out r_result INT)
  BEGIN
    DECLARE impact_rows int DEFAULT 0;
    START TRANSACTION;
    insert ignore into success_seckilled
      (seckill_id, user_phone, create_time)
    values
      (v_seckill_id, v_phone, v_kill_time);
    select row_count() into impact_rows;
    IF (impact_rows < 0) THEN
      ROLLBACK;
      SET r_result = -3; -- inner error
    ELSEIF (impact_rows = 0) THEN
      ROLLBACK;
      SET r_result = -1; -- kill repeat
    ELSE
      update seckill
      set seckill_number = seckill_number - 1
      where seckill_id = v_seckill_id
        and start_time < v_kill_time
        and end_time > v_kill_time
        and seckill_number > 0;
      select row_count() into impact_rows;
      IF (impact_rows < 0) THEN
        ROLLBACK;
        SET r_result = -3; -- inner error
      ELSEIF (impact_rows = 0) THEN
        ROLLBACK;
        SET r_result = 0; -- kill end
      ELSE
        COMMIT;
        SET r_result = 1; -- success
      END IF;
    END IF;
  END;
$$

-- 测试
DELIMITER ;

set @r_result = -4;

call execute_seckill(1003, 18301180912, now(), @r_result);

select @r_result;