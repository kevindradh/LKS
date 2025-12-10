using LatihanSakuraSushi.DTOs;
using LatihanSakuraSushi.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;

namespace LatihanSakuraSushi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthController(SakuraSushiContext _context, IConfiguration _config) : ControllerBase
    {
        [HttpPost("SignIn")]
        public IActionResult SignIn(SignInDTO user)
        {
            var password = HashPassword(user.Password);
            var checkUser = _context.Users
                .FirstOrDefault(f => f.Username == user.Username &&
                                f.PasswordHash == password);

            if (checkUser == null)
            {
                return NotFound(new { Message = "Incorrect username or password!" });
            }

            var genToken = GenerateJwtToken(checkUser);

            return Ok(new
            {
                Token = genToken,
                ExpiredAt = DateTime.Now.AddMinutes(10)
            });
        }

        [HttpGet("Me")]
        [Authorize]
        public IActionResult Me()
        {
            var userId = User.FindFirstValue(ClaimTypes.Sid);
            var convertGuid = Guid.TryParse(userId, out Guid convertedGuid);

            if (!convertGuid)
            {
                return BadRequest(new { Message = "Invalid Guid!" });
            }

            var user = _context.Users.FirstOrDefault(f => f.Id == convertedGuid);

            if (user == null)
            {
                return NotFound(new { Message = "User not found!" });
            }

            return Ok(new
            {
                user.Username,
                user.FullName,
                user.Email,
                user.PhoneNumber,
                user.Role
            });
        }

        private string GenerateJwtToken(Models.User user)
        {
            var jwtKey = _config["Jwt:Key"];
            var convertByte = Encoding.UTF8.GetBytes(jwtKey);
            var securityKey = new SymmetricSecurityKey(convertByte);
            var cSigninCredentials = 
                new SigningCredentials(securityKey, SecurityAlgorithms.HmacSha256);
            var cClaims = new[]
            {
                new Claim(ClaimTypes.Sid, user.Id.ToString())
            };
            var jwtAuth = new JwtSecurityToken(
                signingCredentials: cSigninCredentials,
                expires: DateTime.Now.AddMinutes(10),
                claims: cClaims);

            return new JwtSecurityTokenHandler().WriteToken(jwtAuth);
        }

        private string HashPassword(string password)
        {
            var hashedBytes = SHA256.HashData(Encoding.UTF8.GetBytes(password));
            return BitConverter.ToString(hashedBytes).Replace("-", "").ToLower();
        }
    }
}
