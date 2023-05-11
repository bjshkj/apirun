package io.apirun.statistics.service;

import io.apirun.base.mapper.ReportStatisticsMapper;
import io.apirun.commons.exception.MSException;
import io.apirun.statistics.dto.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
public class ReportStatisticsService {
    @Resource
    private ReportStatisticsMapper reportStatisticsMapper;

    private static final String DAY = "%Y-%m-%d";
    private static final String WEEK = "%Y-%u";
    private static final String MONTH = "%Y-%m";
    private static final String API = "definition";
    private static final String CASE = "case";
    private static final String SCENE = "automation";
    private static final String PER = "performance";


    public CasesTotalDTO getCasesTotal(CaseCountRequest request){
        Long start  = dateConversion((String) request.getBeginTime());
        Long end = dateConversion((String) request.getEndTime());
        request.setBeginTime(start);
        request.setEndTime(end);
        //统计api、case、场景、性能、httprunner的用例数
        Long apiNum = reportStatisticsMapper.countApi(request);
        Long caseNum = reportStatisticsMapper.countCase(request);
        Long sceneNum = reportStatisticsMapper.countScene(request);
        Long perNum = reportStatisticsMapper.countPerformance(request);
        Long httpRunnerNum = reportStatisticsMapper.countHttpRunner(request);

        CasesTotalDTO casesTotalDto = new CasesTotalDTO();
        casesTotalDto.setApiNum(apiNum);
        casesTotalDto.setCaseNum(caseNum);
        casesTotalDto.setSceneNum(sceneNum);
        casesTotalDto.setPerNum(perNum);
        casesTotalDto.setHttpRunnerNum(httpRunnerNum);
        return casesTotalDto;
    }

    public List<ProjectTypeDTO> countProjectType(CaseCountRequest request){
        //统计项目类型数
        Long start  = dateConversion(String.valueOf(request.getBeginTime()));
        Long end = dateConversion(String.valueOf(request.getEndTime()));
        request.setBeginTime(start);
        request.setEndTime(end);
        List<ProjectTypeDTO> list= reportStatisticsMapper.countProjectTypeByWorkerSpacesIDInTimeSpace(request);
        list.forEach(item -> {
            request.setProjectType(item.getProjectType());
            int newNum = reportStatisticsMapper.countProjectTypeByWorkerSpacesIDNewNum(request);
            item.setNewNum(newNum);
        });
        return list;
    }

    public ChartDataDTO getChartData(CaseCountRequest request){
        Long start  = dateConversion(String.valueOf(request.getBeginTime()));
        Long end = dateConversion(String.valueOf(request.getEndTime()));
        List<String> xlist = findDates(String.valueOf(request.getBeginTime()),String.valueOf(request.getEndTime()), request.getDataSpan());
        request.setBeginTime(start);
        request.setEndTime(end);
        //处理前端的时间间隔，让sql按日、周、月获取数据
        if (request.getDataSpan().equalsIgnoreCase("D")){
            request.setDataSpan(DAY);
        }else if (request.getDataSpan().equalsIgnoreCase("W")){
            request.setDataSpan(WEEK);
        }else if (request.getDataSpan().equalsIgnoreCase("M")){
            request.setDataSpan(MONTH);
        }else {
            MSException.throwException("时间间隔格式错误");
        }
        //查询到的这个时间段内的有新增的用例数及相应的日期
        List<EveryDayDataDTO> apiList = reportStatisticsMapper.countApiInTimeSpace(request);
        List<EveryDayDataDTO> caseList = reportStatisticsMapper.countCaseInTimeSpace(request);
        List<EveryDayDataDTO> sceneList = reportStatisticsMapper.countSceneInTimeSpace(request);
        List<EveryDayDataDTO> perList = reportStatisticsMapper.countPerformanceInTimeSpace(request);
        List<EveryDayDataDTO> httpList = reportStatisticsMapper.countHttpRunnerInTimeSpace(request);
        //查询到这个时间段内所有的修改的用例数及相应的日期
        request.setOperType("UPDATE");
        request.setCaseType(API);
        List<EveryDayDataDTO> apiUpdateList = reportStatisticsMapper.countCasesInTimeSpace(request);
        request.setCaseType(CASE);
        List<EveryDayDataDTO> caseUpdateList = reportStatisticsMapper.countCasesInTimeSpace(request);
        request.setCaseType(SCENE);
        List<EveryDayDataDTO> sceneUpdateList = reportStatisticsMapper.countCasesInTimeSpace(request);
        request.setCaseType(PER);
        List<EveryDayDataDTO> perUpdateList = reportStatisticsMapper.countCasesInTimeSpace(request);
        //查询这个时间段内所有的删除的用例数及相应的日期
        request.setOperType("DEL");
        request.setCaseType(API);
        List<EveryDayDataDTO> apiDelList = reportStatisticsMapper.countCasesInTimeSpace(request);
        request.setCaseType(CASE);
        List<EveryDayDataDTO> caseDelList = reportStatisticsMapper.countCasesInTimeSpace(request);
        request.setCaseType(SCENE);
        List<EveryDayDataDTO> sceneDelList = reportStatisticsMapper.countCasesInTimeSpace(request);
        request.setCaseType(PER);
        List<EveryDayDataDTO> perDelList = reportStatisticsMapper.countCasesInTimeSpace(request);
        //给这个时间段内的每天新增、修改、删除的用例数量添加默认值
        Map<String,Integer> map1 = new LinkedHashMap<>();
        if (!xlist.isEmpty()){
            xlist.forEach(item -> {
                map1.put(item,0);
            });
        }
        //每天的新增用例数
        CasesValuesDTO casesCreate = new CasesValuesDTO();
        casesCreate.setOperationType("新增");
        casesCreate.setApiNum(handParam(apiList,xlist));
        casesCreate.setCaseNum(handParam(caseList,xlist));
        casesCreate.setSceNum(handParam(sceneList,xlist));
        casesCreate.setPerNum(handParam(perList,xlist));
        casesCreate.setHttprunnerNum(handParam(httpList,xlist));
        //每天的修改用例数
        CasesValuesDTO casesUpdate = new CasesValuesDTO();
        casesUpdate.setOperationType("修改");
        casesUpdate.setApiNum(handParam(apiUpdateList,xlist));
        casesUpdate.setCaseNum(handParam(caseUpdateList,xlist));
        casesUpdate.setSceNum(handParam(sceneUpdateList,xlist));
        casesUpdate.setPerNum(handParam(perUpdateList,xlist));
        //每天的删除用例数
        CasesValuesDTO casesDel = new CasesValuesDTO();
        casesDel.setOperationType("删除");
        casesDel.setApiNum(handParam(apiDelList,xlist));
        casesDel.setCaseNum(handParam(caseDelList,xlist));
        casesDel.setSceNum(handParam(sceneDelList,xlist));
        casesDel.setPerNum(handParam(perDelList,xlist));

        List<CasesValuesDTO> ylist = new ArrayList<>();
        ylist.add(casesCreate);
        ylist.add(casesUpdate);
        ylist.add(casesDel);
        ChartDataDTO chartDataDTO = new ChartDataDTO();
        chartDataDTO.setXlist(xlist);
        chartDataDTO.setYlists(ylist);
        return chartDataDTO;
    }

    private Long dateConversion(String time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Long day = 0L;
        try {
            Date date = format.parse(time);

            day = (Long) date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

    private List<String> findDates(String begin,String end,String dataSpan){
        List<String> list = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate beginDate = LocalDate.parse(begin,dateTimeFormatter);
        LocalDate endDate = LocalDate.parse(end,dateTimeFormatter);
        while (endDate.isAfter(beginDate) || endDate.equals(beginDate)){
            if (dataSpan.equalsIgnoreCase("W")){
                //获取当前日期所在周的周一
                LocalDate monday = beginDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY,4);
                int weekNumber = monday.get(weekFields.weekOfYear());
                int year = monday.getYear();
                list.add(year + "-" + weekNumber);
                beginDate = monday.plusDays(7);
            }else if (dataSpan.equalsIgnoreCase("D")){
                list.add(String.valueOf(beginDate));
                beginDate = beginDate.plusDays(1);
            }else if (dataSpan.equalsIgnoreCase("M")){
                //获取当前日期所在月的第一天
                LocalDate firstDay = beginDate.with(TemporalAdjusters.firstDayOfMonth());
                list.add(beginDate.getYear() + "-" +beginDate.getMonthValue());
                beginDate = firstDay.plusDays(31);
            }else {
                MSException.throwException("dataSpan类型错误");
            }
        }
        return list;
    }

    private Map<String,Integer> mergeMap(Map<String,Integer> map1,Map<String,Integer> map2){
        Map<String,Integer> combineResultMap = new LinkedHashMap<String, Integer>();
        combineResultMap.putAll(map1);
        combineResultMap.putAll(map2);
        return combineResultMap;
    }

    private List<Integer> handParam(List<EveryDayDataDTO> list, List<String> xlist){
        Map<String,Integer> map = new LinkedHashMap<>();
        Map<String,Integer> map1 = new LinkedHashMap<>();
        if (!xlist.isEmpty()){
            xlist.forEach(item -> {
                map1.put(item,0);
            });
        }
        if (!list.isEmpty()){
            list.forEach(item -> {
                map.put(item.getDate(),item.getCount());
            });
        }
        Map<String,Integer> newMap = mergeMap(map1,map);
        Collection<Integer> values = newMap.values();
        return new ArrayList<>(values);
    }

}
