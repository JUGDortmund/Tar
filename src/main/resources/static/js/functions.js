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
    $('[data-toggle="filter"]').click(function() {
      return $('.offcanvas-filter').toggleClass('active');
    });
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
      events: [
        {
          title: 'Urlaub bpr',
          status: 'pending',
          start: '2015-04-01',
          end: '2015-04-08'
        }, {
          title: 'Long Event',
          start: '2015-02-07',
          end: '2015-02-10'
        }, {
          id: 999,
          title: 'Repeating Event',
          start: '2015-02-09T16:00:00'
        }, {
          id: 999,
          title: 'Repeating Event',
          start: '2015-02-16T16:00:00'
        }, {
          title: 'Confernce',
          start: '2015-02-11',
          end: '2015-02-13'
        }, {
          title: 'Meeting',
          start: '2015-02-12T10:30:00',
          end: '2015-02-12T12:30:00'
        }, {
          title: 'Lunch',
          start: '2015-02-12T12:00:00'
        }, {
          title: 'Meeting',
          start: '2015-02-12T14:30:00'
        }, {
          title: 'Happy Hour',
          start: '2015-02-12T17:30:00'
        }, {
          title: 'Dinner',
          start: '2015-02-12T20:00:00'
        }, {
          title: 'Birthday Party',
          start: '2015-02-13T07:00:00'
        }, {
          title: 'Click for Google',
          url: 'http://google.com/',
          start: '2015-02-28'
        }
      ]
    });
  })(jQuery);

}).call(this);
