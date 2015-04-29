(function() {
  (function($) {
    $('#calendar').fullCalendar({
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
      eventSources: [
        {
          url: '/calendar',
          data: {
            showApproved: $('#showApproved').is(':checked'),
            showPending: $('#showPending').is(':checked'),
            showRejected: $('#showRejected').is(':checked'),
            showCanceled: $('#showCanceled').is(':checked')
          }
        }
      ],
      eventClick: function(calEvent, jsEvent, view) {
        var $vacationDetail;
        $vacationDetail = $('#vacationDetail');
        $vacationDetail.find('.user').text(calEvent.userFirstName + ' ' + calEvent.userLastName);
        $vacationDetail.find('.state').text(calEvent.state);
        $vacationDetail.find('.time').text(calEvent.start.format('DD.MM.YYYY') + ' - ' + calEvent.end.format('DD.MM.YYYY'));
        $vacationDetail.find('.substitue').text(calEvent.substituteFirstName + ' ' + calEvent.substituteLastName);
        $vacationDetail.show();
        return $('#sidebar').addClass('active');
      }
    });
    return $('#calendarFilter .checkbox input').on('click', function() {
      return $('#calendar').fullCalendar('refetchEvents');
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
