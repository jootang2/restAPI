package me.jootang2.inflearn2.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jootang2.inflearn2.common.RestDocksConfiguration;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.print.attribute.standard.Media;
import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@AutoConfigureRestDocs
@Import(RestDocksConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ModelMapper modelMapper;

    @Test
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 11, 28, 22, 50))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 11, 29, 22, 50))
                .beginEventDateTime(LocalDateTime.of(2022, 11, 30, 22, 50))
                .endEventDateTime(LocalDateTime.of(2022, 12, 01, 22, 50))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("부평역 분수대 앞")
                .build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("Description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("begin time of enroll"),
                                fieldWithPath("closeEnrollmentDateTime").description("close time of enroll"),
                                fieldWithPath("beginEventDateTime").description("begin time of event"),
                                fieldWithPath("endEventDateTime").description("close time of event end"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("base of price"),
                                fieldWithPath("maxPrice").description("max of price"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("Description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("begin time of enroll"),
                                fieldWithPath("closeEnrollmentDateTime").description("close time of enroll"),
                                fieldWithPath("beginEventDateTime").description("begin time of event"),
                                fieldWithPath("endEventDateTime").description("close time of event end"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("base of price"),
                                fieldWithPath("maxPrice").description("max of price"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("offline").description("offline or not"),
                                fieldWithPath("free").description("free or not"),
                                fieldWithPath("eventStatus").description("eventStatus"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;

    }

    @Test
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 11, 28, 22, 50))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 11, 29, 22, 50))
                .beginEventDateTime(LocalDateTime.of(2022, 11, 30, 22, 50))
                .endEventDateTime(LocalDateTime.of(2022, 12, 01, 22, 50))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("부평역 분수대 앞")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 11, 28, 22, 50))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 11, 29, 22, 50))
                .beginEventDateTime(LocalDateTime.of(2022, 11, 30, 22, 50))
                .endEventDateTime(LocalDateTime.of(2022, 11, 25, 22, 50))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("부평역 분수대 앞")
                .build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @DisplayName("이벤트 목록 조회")
    public void queryEvents() throws Exception {
        //Given
        for(int i=0; i < 30; i ++){
            generateEvents(i);
        }

        //When
        mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;

    }

    @Test
    @DisplayName("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception{
        //Given
        Event event = generateEvents(100);

        //When & Then
        mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
                ;
    }

    @Test
    @DisplayName("없는 이벤트를 조회했을 때 404 응답받기")
    public void getEvent404() throws Exception{
        mockMvc.perform(get("/api/events/11532"))
                .andExpect(status().isNotFound())
                ;
    }

    @Test
    @DisplayName("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception{
        //Given
        Event event = generateEvents(200);
        String eventName = "Updated Event";
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setName(eventName);

        //When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(print())
                ;
    }

    @Test
    @DisplayName("입력값이 비어있는 경우 이벤트 수정 실패")
    public void updateEvent400_Empty() throws Exception{
        //Given
        Event event = generateEvents(200);
        String eventName = "Updated Event";
        EventDto eventDto = new EventDto();

        //When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("입력값이 잘못된 경우 이벤트 수정 실패")
    public void updateEvent400_Wrong() throws Exception{
        //Given
        Event event = generateEvents(200);
        String eventName = "Updated Event";
        EventDto eventDto = modelMapper.map(event,EventDto.class);
        eventDto.setBasePrice(2000);
        eventDto.setMaxPrice(100);

        //When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("입력값이 잘못된 경우 이벤트 수정 실패")
    public void updateEvent404() throws Exception{
        //Given
        Event event = generateEvents(200);
        String eventName = "Updated Event";
        EventDto eventDto = modelMapper.map(event,EventDto.class);

        //When & Then
        mockMvc.perform(put("/api/events/123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    private Event generateEvents(int index) {
        Event event = Event.builder()
                .name("event" + index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 11, 28, 22, 50))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 11, 29, 22, 50))
                .beginEventDateTime(LocalDateTime.of(2022, 11, 30, 22, 50))
                .endEventDateTime(LocalDateTime.of(2022, 12, 01, 22, 50))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("부평역 분수대 앞")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build()
                ;
        return eventRepository.save(event);

    }

}
