package com.urlshortener.controller;


import com.urlshortener.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PageController {

    UrlShortenerService urlShortenerService;

    @GetMapping("/")
    public String indexPage(){
        return "index";
    }

    /**
     * This method handles the form submission from our index.html page.
     *
     * @PostMapping("/shorten-web"): This maps the method to handle POST requests sent
     *                               to the "/shorten-web" endpoint, matching our form's action.
     * @param longUrl This parameter is bound to the form's input data.
     *                The @RequestParam("longUrl") annotation tells Spring to find the value
     *                associated with the form input field that has name="longUrl" and
     *                assign it to this String variable.
     * @param model   The Model object is a map-like container provided by Spring. We will use
     *                it in the upcoming tasks to pass data (like the result of the shortening)
     *                from the controller back to the Thymeleaf view for rendering.
     * @return        The string "index", which tells Spring to re-render the index.html page.
     *                This way, the user stays on the same page to see the result.
     */
    @PostMapping("/shorten-web")
    public String handleShortenForm(@RequestParam("longUrl") String longUrl, Model model) {

        String shortCode = urlShortenerService.shortenUrl(longUrl);

        String fullShortUrl = "http://localhost:8080/" + shortCode;

        model.addAttribute("originalUrl", longUrl);

        model.addAttribute("shortUrlResult", fullShortUrl);
        return "index";
    }
}
