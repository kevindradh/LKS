using BelajarAGS.DTOs;
using BelajarAGS.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using System.ComponentModel.DataAnnotations;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;
using System.Text.RegularExpressions;

namespace BelajarAGS.Controllers
{
    [Route("gsa-api/v1/[controller]")]
    [ApiController]
    public class usersController(GsaContext _context, IConfiguration _config) : ControllerBase
    {
        [HttpPost("register")]
        public IActionResult Register(RegisterDTO user)
        {
            bool validateEmail = new EmailAddressAttribute().IsValid(user.Email);

            if (!validateEmail)
            {
                return UnprocessableEntity(new { Message = "Invalid email address!" });
            }

            bool checkEmail = _context.Users.Any(f => f.Email == user.Email);

            if (checkEmail)
            {
                return UnprocessableEntity(new { Message = "Email has already used!" });
            }

            bool checkPassword = IsValidPassword(user.Password);

            if (!checkPassword)
            {
                return UnprocessableEntity(new { Message = "Password must follow the required format (minimum 8 characters with an uppercase, a lowercase, a symbol, and a number)!" });
            }

            string hashPassword = HashPassword(user.Password);

            Models.User newUser = new Models.User()
            {
                Email = user.Email,
                PasswordHash = hashPassword,
                Username = user.Username,
                Name = user.FullName,
                Role = "student"
            };

            _context.Users.Add(newUser);
            _context.SaveChanges();

            return Ok(new { Message = "User registered successfully." });
        }

        [HttpPost("login")]
        public IActionResult Login(LoginDTO login)
        {
            var pwd = HashPassword(login.Password);
            var check = _context.Users.FirstOrDefault(f => f.Email == login.Email && f.PasswordHash == pwd);

            if (check == null)
            {
                return Unauthorized(new { Message = "Invalid email or password" });
            }

            var genJwt = GenerateJWT(check);

            var dataLogin = new
            {
                Message = "Login successfull.",
                Data = new
                {
                    UserId = check.Id,
                    Username = check.Username,
                    Role = check.Role,
                    Token = genJwt
                }
            };

            return Ok(dataLogin);
        }

        private string GenerateJWT(User usr)
        {
            var key = _config["Jwt:Key"];
            var encode = Encoding.UTF8.GetBytes(key);

            var securityKey = new SymmetricSecurityKey(encode);
            var cSigninCredentials = new SigningCredentials(securityKey, SecurityAlgorithms.HmacSha256);

            var cClaims = new[]
            {
                new Claim(ClaimTypes.NameIdentifier, usr.Id.ToString()),
                new Claim(ClaimTypes.Role, usr.Role)
            };

            var jwtAuth = new JwtSecurityToken(
                signingCredentials: cSigninCredentials,
                claims: cClaims,
                expires: DateTime.Now.AddDays(7)
                );

            return new JwtSecurityTokenHandler().WriteToken(jwtAuth);
        }

        private string HashPassword(string password)
        {
            using (SHA256 sha256 = SHA256.Create())
            {
                byte[] inputBytes = Encoding.UTF8.GetBytes(password);
                byte[] hashBytes = sha256.ComputeHash(inputBytes);

                StringBuilder sb = new StringBuilder();
                foreach (byte b in hashBytes)
                {
                    sb.Append(b.ToString("x2"));
                }

                return sb.ToString();
            }
        }

        private bool IsValidPassword(string password)
        {
            string pattern = @"^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\da-zA-Z]).{8,}$";
            return Regex.IsMatch(password, pattern);
        }
    }
}
