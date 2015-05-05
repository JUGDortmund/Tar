(function() {
  (function($) {
    var getCurrentCheckedStatus;
    getCurrentCheckedStatus = function(checkboxId) {
      return $(checkboxId).is(':checked');
    };
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
          data: function() {
            return {
              showApproved: $('#showApproved').is(':checked'),
              showPending: $('#showPending').is(':checked'),
              showRejected: $('#showRejected').is(':checked'),
              showCanceled: $('#showCanceled').is(':checked')
            };
          }
        }
      ],
      eventDataTransform: function(eventData) {
        switch (eventData.state) {
          case 'approved':
            eventData.color = '#008d4c';
            break;
          case 'pending':
            eventData.color = '#f39c12';
            break;
          case 'rejected':
            eventData.color = '#dd4b39';
            break;
          case 'canceled':
            eventData.color = '#ca195a';
            break;
          default:
            eventData.color = '#00f';
        }
        eventData.displayedEnd = moment(eventData.end);
        eventData.end = moment(eventData.end).add(1, 'days');
        return eventData;
      },
      eventClick: function(calEvent, jsEvent, view) {
        var $vacationDetail, managerText, substituteText;
        $vacationDetail = $('#vacationDetail');
        $vacationDetail.find('.user').text(calEvent.userFirstName + ' ' + calEvent.userLastName);
        $vacationDetail.find('.state').text(calEvent.state);
        $vacationDetail.find('.time').text(calEvent.start.format('DD.MM.YYYY') + ' - ' + calEvent.displayedEnd.format('DD.MM.YYYY'));
        if ((calEvent.substituteFirstName !== null) && (calEvent.substituteLastName !== null)) {
          substituteText = calEvent.substituteFirstName + ' ' + calEvent.substituteLastName;
        } else {
          substituteText = '-';
        }
        $vacationDetail.find('.substitute').text(substituteText);
        if ((calEvent.managerFirstName !== null) && (calEvent.managerLastName !== null)) {
          managerText = calEvent.managerFirstName + ' ' + calEvent.managerLastName;
        } else {
          managerText = '-';
        }
        $vacationDetail.find('.manager').text(managerText);
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
  var refreshVacationForm;

  refreshVacationForm = function(data) {
    var $myForm, clientWidth;
    clientWidth = document.documentElement.clientWidth;
    if (clientWidth < 770) {
      $('html, body').animate({
        scrollTop: ($('.vacation-form').offset().top)
      }, 'slow');
    }
    $myForm = $('#vacation-form-panel');
    $myForm.html(data).hide().fadeIn(800);
    $('.panel-default').matchHeight();
    $myForm.find('.input-group.date').datepicker({});
    return $myForm.find('select').select2();
  };

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
    return $('.vacation-list a, .task-list a').click(function() {
      $.ajax({
        url: $(this).attr('href'),
        dataType: "html",
        error: function(jqXHR, textStatus, errorThrown) {
          return console.log(textStatus);
        },
        success: function(data) {
          return refreshVacationForm(data);
        }
      });
      return false;
    });
  })(jQuery);

}).call(this);
