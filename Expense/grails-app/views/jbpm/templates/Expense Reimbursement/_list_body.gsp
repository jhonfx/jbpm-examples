<tr>
    <td><g:link action="details" id="${taskInstance.id}">${taskInstance.id}</g:link></td>
    <td>${taskInstance.variables['expenseClaim']?.purpose}</td>
    <td>${taskInstance.variables['expenseClaim']?.date}</td>
    <td>${taskInstance.variables['expenseClaim']?.amount}</td>
    <td>${taskInstance.create}</td>
</tr>