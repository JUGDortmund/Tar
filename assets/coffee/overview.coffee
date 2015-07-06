#Scroll to manualEntry-form in small resolutions
scrollToVacationManualEntryForm = ->
  clientWidth = document.documentElement.clientWidth;
  if clientWidth < 761 then $('html, body').animate({ scrollTop: ($('#entry-form-panel').offset().top - 20)}, 'slow')

showManualEntryForm = (data) ->
    console.log 'book it!'
    $myFilter = $('#filter-form-panel')
    $myFilter.hide()
    $myForm = $('#entry-form-panel')
    $myForm.html(data).hide().show()
    scrollToVacationManualEntryForm()
    $('.offcanvas-filter').show()
    $myForm.find('select').select2()

hideManualEntryForm = (data) ->
    $myForm = $('#entry-form-panel')
    $myForm.hide( )
    $myFilter = $('#filter-form-panel')
    $myFilter.hide().show()

# document ready
(($) ->
    hideManualEntryForm()
    $('[data-toggle="tooltip"]').tooltip()
    $('#employees').select2()
    $('#employees').off('select2-opening')
    $('.select2-search__field').focus()

    $('#employees').on 'select2:unselecting', (e) ->
        $(this).data 'state', 'unselected'
        return

    $('#employees').on 'select2:opening ', (e) ->
        if ($(this).data('state') == 'unselected')
            $(this).removeData 'state'
            e.preventDefault()
        return

    $('#clear').on 'click', (e) ->
        $('#employees').val(null).trigger('change')
        return

    $('.manual-entry').click ->
        $.ajax
            url: $(this).attr('href')
            dataType: "html"
            error: (jqXHR, textStatus, errorThrown) ->
                console.log(textStatus)
            success: (data) ->
                showManualEntryForm(data)
        return false

)(jQuery);