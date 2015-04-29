(function() {
  (function($) {
    return $('#calendar').fullCalendar({
      header: {
        left: 'prev,next today',
        center: 'title',
        right: 'month,basicWeek'
      },
      defaultView: 'month',
      lang: 'de',
      weekends: false,
      eventLimit: true,
      weekNumbers: true,
      events: {
        url: 'myData.json'
      },
      eventClick: function(calEvent, jsEvent, view) {
        console.log('Event: ' + calEvent);
        return $(this).css('border-color', 'red');
      }
    });
  })(jQuery);

}).call(this);

(function() {
  (function($) {
    $('.input-group.date').datepicker({
      "format": "dd.mm.yyyy"
    });
    $('.panel-default select').select2();
    $('.autosubmit').on('change', function() {
      return $(this).closest('form').submit();
    });
    $('.panel-default').matchHeight();
    return $('[data-toggle="filter"]').click(function() {
      return $('.offcanvas-filter').toggleClass('active');
    });
  })(jQuery);

}).call(this);
