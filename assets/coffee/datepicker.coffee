( ($) ->
  
  $.fn.datepicker.dates['en'] = {
    days: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]
    daysShort: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]
    daysMin: ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"]
    months: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]
    monthsShort: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
    today: "Today"
    clear: "Clear"
  }

  $.fn.datepicker.dates['de'] = {
    days: ["Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"]
    daysShort: ["Son", "Mon", "Die", "Mit", "Don", "Fre", "Sam", "Son"]
    daysMin: ["So", "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"]
    months: ["Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"]
    monthsShort: ["Jan", "Feb", "Mär", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez"]
    today: "Heute"
    clear: "Löschen"
    weekStart: 1
  }
)(jQuery);