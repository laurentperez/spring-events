/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sambrannen.samples.events.web;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;

import com.sambrannen.samples.events.domain.Event;
import com.sambrannen.samples.events.repository.EventRepository;

/**
 * RESTful controller for {@link Event events}.
 *
 * @author Sam Brannen
 * @since 1.0
 */
@RestController
@RequestMapping("/events")
public class RestEventController {

	private final EventRepository repository;


	@Autowired
	public RestEventController(EventRepository eventRepository) {
		this.repository = eventRepository;
	}

	@RequestMapping(method = GET)
	public List<Event> retrieveAllEvents() {
		return repository.findAll();
	}

	@RequestMapping(method = POST)
	@ResponseStatus(HttpStatus.CREATED)
	public void createEvent(@RequestBody Event postedEvent, HttpServletRequest request, HttpServletResponse response) {
		Event savedEvent = repository.save(postedEvent);
		String newLocation = buildNewLocation(request, savedEvent.getId());
		response.setHeader("Location", newLocation);
	}

	@RequestMapping(value = "/{id}", method = GET)
	public Event retrieveEvent(@PathVariable Long id) {
		return repository.findOne(id);
	}

	@RequestMapping(value = "/{id}", method = DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteEvent(@PathVariable Long id) {
		repository.delete(id);
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public void handleDatabaseConstraintViolation() {
		/* no-op */
	}

	private String buildNewLocation(HttpServletRequest request, Long id) {
		String url = request.getRequestURL().append("/{id}").toString();
		UriTemplate uriTemplate = new UriTemplate(url);
		return uriTemplate.expand(id).toASCIIString();
	}

}