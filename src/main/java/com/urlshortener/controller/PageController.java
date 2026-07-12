package com.urlshortener.controller;


import com.urlshortener.dto.UrlStatsResponse;
import com.urlshortener.exception.AliasAlreadyExistsException;
import com.urlshortener.exception.UrlNotFoundException;
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
    public String handleShortenForm(@RequestParam("longUrl") String longUrl,@RequestParam(name = "customAlias", required = false) String customAlias, Model model) {

        model.addAttribute("originalUrl", longUrl);

        try {
            String shortCode = urlShortenerService.shortenUrl(longUrl, customAlias, null);

            String fullShortUrl = "http://localhost:8080/" + shortCode;
            model.addAttribute("shortUrlResult", fullShortUrl);

        } catch (AliasAlreadyExistsException e) {
            model.addAttribute("aliasError", e.getMessage());
        }

        return "index";
    }

    /**
     * Handles the form submission for checking URL statistics.
     *
     * @PostMapping("/check-stats"): Maps POST requests from our new stats form to this method.
     * @param shortCode The @RequestParam("checkShortCode") annotation tells Spring to find the
     *                  form data with the key "checkShortCode" (matching our input's 'name' attribute)
     *                  and inject its value into this String parameter.
     * @param model     The Model object, which we will use in the next task to pass the
     *                  retrieved statistics back to the view for rendering.
     * @return The string "index", telling Spring to re-render the index.html page to display the results.
     */
    @PostMapping("/check-stats")
    public String handleStatsCheckForm(@RequestParam("checkShortCode") String shortCode, Model model) {


        try {
            UrlStatsResponse stats = urlShortenerService.getStats(shortCode);
            model.addAttribute("urlStats", stats);
        } catch (UrlNotFoundException e) {
            model.addAttribute("statsError", "Statistics not found for short code: " + shortCode);
        }

        return "index";
    }
}
